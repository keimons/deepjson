package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.ElementsFuture;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ArrayUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Object[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ObjectArrayCodec extends BaseArrayCodec<Object[]> {

	public static final ObjectArrayCodec instance = new ObjectArrayCodec();

	@Override
	public void build(AbstractContext context, Object[] value) {
		context.cache(new ElementsFuture(value.length), EmptyCodec.instance);
		for (Object obj : value) {
			context.build(obj);
		}
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Object[] value, int uniqueId, long options) {
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		super.encode(context, buf, value, uniqueId, options);
	}

	@Override
	public void encode0(AbstractContext context, AbstractBuffer buf, Object[] values, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			context.encode(buf, options);
		}
	}

	@Override
	public Object[] decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.LBRACKET) {
			// 原生进入 [x, y, z]
			return decode0(context, buf, type, options);
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> clazz = typeCheck(context, buf, options);
		if (clazz != null) {
			if (!Object[].class.isAssignableFrom(clazz)) { // 必须是 对象数组类型 或 子类
				throw new IncompatibleTypeException(clazz, Object[].class);
			}
			// TODO 安全性检查
			type = clazz;
			token = buf.nextToken();
		}
		int uniqueId = -1;
		Object[] value = null;
		for (; ; ) {
			// 断言当前位置一定是一个对象
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 判断是否 "@id"
			if (token == SyntaxToken.STRING && buf.checkPutId()) {
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(numberExpects, stringExpects);
				uniqueId = buf.intValue();
			} else if (token == SyntaxToken.STRING && buf.checkGetValue()) {
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
				value = decode0(context, buf, type, options);
			} else if (false) {
				// TODO 新增宽松的解决方案
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS); // 预期当前语法是一个对象
				context.decode(buf, Object.class, false, options); // 读取一个对象
			} else {
				throw new UnknownSyntaxException("array error");
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.nextToken();
		}
		if (uniqueId != -1) {
			context.put(uniqueId, value);
		}
		return value;
	}

	public Object[] decode0(final IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		Type instanceType = findInstanceType(type);
		List<Object> values = new ArrayList<Object>();
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
				hooks[count++] = values.size();
				hooks[count++] = buf.get$Id();
				values.add(null); // hold on
			} else {
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
				values.add(context.decode(buf, instanceType, false, options));
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		Class<?> clazz = findInstanceType0(context, instanceType);
		final Object[] result = ArrayUtil.newInstance(clazz, values.size());
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		if (hooks != null) {
			for (int i = 0; i < count; i += 2) {
				final int index = hooks[i];
				final int unique = hooks[i + 1];
				context.addCompleteHook(new Runnable() {
					@Override
					public void run() {
						result[index] = context.get(unique);
					}
				});
			}
		}
		return result;
	}

	/**
	 * 获取数组的组件类型
	 *
	 * @param type {@link GenericArrayType}泛型数组类型或{@code Object[]}对象数组类型。
	 * @return 组件类型
	 */
	private Type findInstanceType(Type type) {
		if (type instanceof GenericArrayType) {
			return ((GenericArrayType) type).getGenericComponentType();
		} else {
			return ((Class<?>) type).getComponentType();
		}
	}

	/**
	 * 获取类型的数组类型
	 *
	 * @param type 类型
	 * @return 数组类型
	 */
	private Class<?> findInstanceType0(IDecodeContext context, Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) type;
			Class<?> clazz = (Class<?>) variable.getGenericDeclaration();
			String name = variable.getName();
			return findInstanceType0(context, context.findType(clazz, name));
		}
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}
		if (type instanceof GenericArrayType) {
			GenericArrayType at = (GenericArrayType) type;
			Class<?> clazz = findInstanceType0(context, at.getGenericComponentType());
			return Array.newInstance(clazz, 0).getClass();
		}
		if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] upperBounds = wt.getUpperBounds();
			if (upperBounds.length == 1) {
				Type upperBoundType = upperBounds[0];
				throw new RuntimeException("unsupported");
			}
		}
		throw new RuntimeException("unsupported");
	}
}