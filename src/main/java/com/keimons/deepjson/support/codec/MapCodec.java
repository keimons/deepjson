package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ElementsFuture;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * {@link Map}编解码器
 * <p>
 * 如果json中有描述自身类型的字段，则跳转到相应的解码器中解码。例如：
 * <pre>
 *     String json = "{"$type":"[Z","$values"=[true, false, "true", "false"]}"\
 *     MapCodec#decode(IDecodeContext, Buffer, Type, long);
 *     --- typeCheck() ---
 *     BaseArrayCodec#decode(IDecodeContext, Buffer, Type, long);
 * </pre>
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MapCodec extends AbstractClassCodec<Object> {

	public static final MapCodec instance = new MapCodec();

	@Override
	public void build(WriterContext context, Object value) {
		Map<?, ?> map = (Map<?, ?>) value;
		ElementsFuture future = new ElementsFuture();
		context.cache(future, EmptyCodec.instance);
		for (Map.Entry<?, ?> obj : map.entrySet()) {
			context.build(obj.getKey());
			context.build(obj.getValue());
			future.addCount();
		}
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		char mark = '{';
		// write class name
		if (CodecOptions.WriteClassName.isOptions(options)) {
			Class<?> clazz = value.getClass();
			if (CodecConfig.WHITE_MAP.contains(clazz)) {
				buf.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		buf.writeMark(mark);
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		int count = ((ElementsFuture) future).getCount();
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			context.encode(buf, CodecModel.K, options);
			buf.writeMark(':');
			context.encode(buf, CodecModel.V, options);
		}
		buf.writeMark('}');
	}

	@Override
	public Object decode(final ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> excepted = typeCheck(context, buf, options);
		// 尝试解析成Map时，判断是否为Map，如果不是，转成相应的类型进行解析
		if (excepted != null) {
			if (!Map.class.isAssignableFrom(excepted)) {
				// Map结构的包装类型，例如：{"$type":"int[]","@id":1,"$value":[1,2,3]}，跳转解析方案
				// TODO 考虑安全漏洞
				return context.decode(buf, excepted, options);
			}
			if (!clazz.isAssignableFrom(excepted)) {
				throw new IncompatibleTypeException(clazz, excepted);
			}
			// 如果是 "," 则表示这一段结束，如果是 "}" 则表示对象结束。
			buf.assertExpectedSyntax(SyntaxToken.COMMA, SyntaxToken.RBRACE);
			// 例如：{"$type":"java.util.HashMap","key":"value"}需要移动token到"key"位置。
			if (buf.token() == SyntaxToken.COMMA) {
				buf.nextToken();
			}
		}
		Class<?> instanceType = excepted == null ? clazz : excepted;
		Type kt = context.findType(Map.class, "K");
		Type vt = context.findType(Map.class, "V");
		final Map<Object, Object> instance = createInstance(instanceType, kt, vt);
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.RBRACE) {
			return instance;
		}
		for (; ; ) {
			// 断言当前位置一定是一个对象
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 抹掉kt类型，判断是否 "@id"
			if (!isInstanceId(instance, context, buf, options)) {
				final int kid;
				final Object key;
				// key 也有可能是一个循环引用
				if (buf.token() == SyntaxToken.STRING && buf.is$Id()) {
					kid = buf.get$Id();
					key = null;
				} else {
					kid = -1;
					// 常规 key-value 结构
					key = context.decode(buf, kt, options);
				}
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				token = buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
				final int vid;
				final Object value;
				if (token == SyntaxToken.STRING && buf.is$Id()) {
					vid = buf.get$Id();
					value = null;
				} else {
					vid = -1;
					// 常规 key-value 结构
					value = context.decode(buf, vt, options);
				}
				if (kid != -1 || vid != -1) {
					context.addCompleteHook(new Runnable() {
						@Override
						public void run() {
							Object k = key;
							Object v = value;
							if (kid != -1) {
								k = context.get(kid);
							}
							if (vid != -1) {
								v = context.get(vid);
							}
							instance.put(k, v);
						}
					});
				} else {
					instance.put(key, value);
				}
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.nextToken();
		}
		return instance;
	}

	/**
	 * 根据{@link Type}创建一个实例
	 * <p>
	 * 我们已经确定是要创建一个实现了{@link Map}类型接口的实例。
	 *
	 * @param clazz 类型
	 * @return 对象
	 */
	private @NotNull Map<Object, Object> createInstance(Class<?> clazz, Type kt, Type vt) {
		// 接口
		if (clazz.isInterface()) {
			return createInterface(clazz);
		}
		// 枚举
		if (EnumMap.class.isAssignableFrom(clazz)) {
			return createEnumMap(kt);
		}
		// 抽象类
		if (Modifier.isAbstract(clazz.getModifiers())) {
			return createAbstract(clazz);
		}
		// 反射创建
		try {
			return ReflectionUtil.newInstance(clazz);
		} catch (Throwable cause) {
			throw new IllegalArgumentException("cannot instantiate map class: " + clazz.getName(), cause);
		}
	}

	/**
	 * 根据接口创建一个{@link Map}实例
	 *
	 * @param clazz 接口
	 * @return {@link Map}实例
	 */
	private Map<Object, Object> createInterface(Class<?> clazz) {
		if (Map.class == clazz) {
			return new HashMap<Object, Object>();
		} else if (SortedMap.class == clazz || NavigableMap.class == clazz) {
			return new TreeMap<Object, Object>();
		} else if (ConcurrentMap.class == clazz) {
			return new ConcurrentHashMap<Object, Object>();
		} else if (ConcurrentNavigableMap.class == clazz) { // java 1.6
			return new ConcurrentSkipListMap<Object, Object>();
		} else {
			throw new IllegalArgumentException("unsupported map interface: " + clazz.getName());
		}
	}

	/**
	 * 根据抽象类创建一个{@link Map}实例
	 *
	 * @param clazz 抽象类
	 * @return {@link Map}实例
	 */
	private Map<Object, Object> createAbstract(Class<?> clazz) {
		// 犀利
		if (AbstractMap.class == clazz) {
			return new HashMap<Object, Object>();
		} else {
			throw new IllegalArgumentException("unsupported map abstract: " + clazz.getName());
		}
	}

	/**
	 * 根据抽象类创建一个{@link EnumMap}实例
	 *
	 * @param kt {@link EnumMap}中{@code K}的类型
	 * @return {@link EnumMap}实例
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private Map<Object, Object> createEnumMap(Type kt) {
		if (!(kt instanceof Class) || !((Class<?>) kt).isEnum()) {
			throw new IllegalArgumentException("unrecognized enum map of key type: " + kt);
		}
		// 抑制警告 已经确认过 "K" 的类型是enum。
		return new EnumMap((Class<?>) kt);
	}

	/**
	 * 使用{@link MethodHandles.Lookup}创建实例
	 *
	 * @param clazz 对象类
	 * @return {@link Map}实例
	 * @throws Throwable 调用异常
	 * @since 9
	 */
	private Map<Object, Object> createMethodHandle(Class<?> clazz) throws Throwable {
		return ReflectionUtil.newInstance(clazz);
	}
}