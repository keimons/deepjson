package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@link Collection}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CollectionCodec extends BaseCodec<Collection<?>> {

	public static final CollectionCodec instance = new CollectionCodec();

	public static final Map<Class<?>, Class<?>> interfaces = new HashMap<Class<?>, Class<?>>();

	static {
		interfaces.put(Collection.class, ArrayList.class);
		interfaces.put(List.class, ArrayList.class);
		interfaces.put(Queue.class, LinkedList.class);
		interfaces.put(Deque.class, LinkedList.class);
		interfaces.put(BlockingQueue.class, LinkedBlockingQueue.class);
		interfaces.put(BlockingDeque.class, LinkedBlockingDeque.class);
		interfaces.put(Set.class, HashSet.class);
		interfaces.put(SortedSet.class, TreeSet.class);
		interfaces.put(NavigableSet.class, TreeSet.class);
	}

	@Override
	public void build(AbstractContext context, Collection<?> value) {
		ElementsFuture future = new ElementsFuture();
		context.cache(future, EmptyCodec.instance);
		for (Object obj : value) {
			context.build(obj);
			future.addCount();
		}
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Collection<?> value, int uniqueId, long options) {
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		int count = ((ElementsFuture) future).getCount();
		char mark = '{';
		// write class name
		boolean className = CodecOptions.WriteClassName.isOptions(options);
		if (className) {
			Class<?> clazz = value.getClass();
			if (Config.WHITE_COLLECTION.contains(clazz)) {
				buf.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		if (uniqueId >= 0 || className) {
			buf.writeName(mark, FIELD_VALUE);
		}
		buf.writeMark('[');
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			context.encode(buf, CodecModel.V, options);
		}
		buf.writeMark(']');
		if (uniqueId >= 0 || className) {
			buf.writeMark('}');
		}
	}

	@Override
	public Collection<?> decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.LBRACKET) {
			Type et = context.findType(Collection.class, "E");
			Class<?> clazz = null;
			if (type instanceof ParameterizedType) {
				clazz = (Class<?>) ((ParameterizedType) type).getRawType();
			} else if (type instanceof Class) {
				clazz = (Class<?>) type;
			} else if (type instanceof WildcardType) {
				throw new RuntimeException();
			}
			final Collection<Object> instance = createInstance(clazz, et);
			// 原生进入 [x, y, z]
			decode0(instance, context, buf, et, options);
			return instance;
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> clazz = typeCheck(context, buf, options);
		if (clazz != null) {
			if (!Collection.class.isAssignableFrom(clazz)) { // 必须是 集合类型 或 子类
				throw new IncompatibleTypeException(clazz, Object[].class);
			}
			// TODO 安全性检查
			type = clazz;
			token = buf.nextToken();
		}
		Type et = context.findType(Collection.class, "E");
		final Collection<Object> instance = createInstance(clazz != null ? clazz : type, et);
		if (token == SyntaxToken.STRING && buf.checkPutId()) {
			buf.nextToken();
			buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			context.put(buf.intValue(), instance);
		}
		for (; ; ) {
			// 断言当前位置一定是一个对象
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 判断是否 "@id"
			if (token == SyntaxToken.STRING && buf.checkGetValue()) {
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
				decode0(instance, context, buf, type, options);
			} else {
				throw new UnknownSyntaxException("array error");
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.nextToken();
		}
		return instance;
	}

	private void decode0(final Collection<Object> instance, final IDecodeContext context, ReaderBuffer buf, Type et, long options) {
		int[] hooks = null;
		int count = 0;
		for (; ; ) {
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.STRING && buf.is$Id()) {
				if (hooks == null) {
					hooks = new int[16]; // 准备8个引用
				}
				if (count >= hooks.length) {
					hooks = Arrays.copyOf(hooks, hooks.length << 1);
				}
				int index = (count >> 1) + instance.size(); // 用于记录位置
				hooks[count++] = index;
				hooks[count++] = buf.get$Id();
			} else {
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
				instance.add(context.decode(buf, et, false, options));
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}

		if (hooks != null) {
			for (int i = 0; i < count; i += 2) {
				final int index = hooks[i];
				final int unique = hooks[i + 1];
				context.addCompleteHook(new Runnable() {
					@Override
					public void run() {
						List<Object> list = new ArrayList<Object>(instance);
						instance.clear();
						instance.addAll(list.subList(0, index));
						instance.add(context.get(unique));
						instance.addAll(list.subList(index, list.size()));
					}
				});
			}
		}
	}

	/**
	 * 创建一个{@link Collection}实例。
	 *
	 * @param type 集合类型
	 * @param et   集合中元素类型
	 * @return 集合实例
	 */
	@SuppressWarnings("unchecked")
	public Collection<Object> createInstance(Type type, Type et) {
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isInterface()) {
				return createInterface(clazz);
			}
			if (EnumSet.class.isAssignableFrom(clazz)) {
				return createEnum(et);
			}
			if (Modifier.isAbstract(clazz.getModifiers())) {
				return createAbstract(clazz);
			}
			try {
				return (Collection<Object>) clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new InstantiationFailedException("create instance error, class " + ((Class<?>) type).getName());
			}
		}
		if (type instanceof ParameterizedType) {
			// 不使用这里的泛型，而是使用上下文中查找到的泛型
			return createInstance(((ParameterizedType) type).getRawType(), et);
		}
		throw new InstantiationFailedException("create instance error, type " + type.getTypeName());
	}

	/**
	 * 根据接口创建一个{@link Collection}实例
	 *
	 * @param clazz 接口
	 * @return {@link Collection}实例
	 */
	@SuppressWarnings("unchecked")
	private Collection<Object> createInterface(Class<?> clazz) {
		Class<?> target = interfaces.get(clazz);
		if (target == null) {
			throw new InstantiationFailedException("create instance error, interface " + clazz.getName());
		} else {
			try {
				// always successful
				return (Collection<Object>) target.getConstructor().newInstance();
			} catch (Exception e) {
				throw new InstantiationFailedException("create instance error, class " + target.getName(), e);
			}
		}
	}

	/**
	 * 根据抽象类创建一个{@link EnumSet}实例
	 *
	 * @param et {@link EnumSet}中{@code K}的类型
	 * @return {@link EnumSet}实例
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private Collection<Object> createEnum(Type et) {
		if (!(et instanceof Class) || !((Class<?>) et).isEnum()) {
			throw new InstantiationFailedException("unrecognized enum set of type: " + et);
		}
		// 抑制警告 已经确认过 "K" 的类型是enum。
		return EnumSet.noneOf((Class<? extends Enum>) et);
	}

	/**
	 * 根据抽象类创建一个{@link Collection}实例
	 *
	 * @param clazz 抽象类
	 * @return {@link Collection}实例
	 */
	private Collection<Object> createAbstract(Class<?> clazz) {
		// 犀利
		if (AbstractCollection.class == clazz || AbstractList.class == clazz) {
			return new ArrayList<Object>();
		} else if (AbstractQueue.class == clazz) {
			return new LinkedList<Object>();
		} else if (AbstractSet.class == clazz) {
			return new HashSet<Object>();
		} else {
			throw new InstantiationFailedException("unrecognized abstract of type: " + clazz.getName());
		}
	}
}