package com.keimons.deepjson.support.context;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.lang.reflect.*;
import java.util.*;

/**
 * 上下文环境
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Context implements IDecodeContext {

	Map<Integer, Object> context = new HashMap<Integer, Object>();

	List<Runnable> hooks = new ArrayList<Runnable>();

	Type[] types = new Type[32];

	int count;

	/**
	 * 增加一个类型
	 *
	 * @param type 类型
	 */
	public void add(Type type) {
		if (count >= types.length) {
			types = Arrays.copyOf(types, types.length << 1);
		}
		types[count++] = type;
	}

	/**
	 * 移除一个类型
	 */
	public void poll() {
		types[--count] = null;
	}

	@Override
	public void put(int uniqueId, Object value) {
		context.put(uniqueId, value);
	}

	@Override
	public Object get(int uniqueId) {
		return context.get(uniqueId);
	}

	@Override
	public Class<?> findClass(Type type) {
		// 普通类型
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		// 参数类型
		if (type instanceof ParameterizedType) {
			return findClass(((ParameterizedType) type).getRawType());
		}
		// 泛型参数
		if (type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) type;
			Class<?> clazz = (Class<?>) variable.getGenericDeclaration();
			String name = variable.getName();
			return findClass(findType(clazz, name));
		}
		// 通配类型
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			// 上界通配符
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (upperBounds.length == 1 && upperBounds[0] != Object.class) {
				return findClass(upperBounds[0]);
			}
			// 下界通配符
			Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds.length == 1 && lowerBounds[0] != Object.class) {
				return findClass(lowerBounds[0]);
			}
			if (upperBounds[0] == Object.class && lowerBounds[0] == Object.class) {
				return Object.class;
			}
			throw new TypeNotFoundException("unknown wildcard type " + type.getTypeName());
		}
		// 泛型数组
		if (type instanceof GenericArrayType) {
			GenericArrayType at = (GenericArrayType) type;
			Class<?> clazz = findClass(at.getGenericComponentType());
			return Array.newInstance(clazz, 0).getClass();
		}
		throw new TypeNotFoundException("unknown type " + type.getTypeName());
	}

	@Override
	public Type findType(Class<?> target, String name) {
		return ClassUtil.findGenericType(types, count, target, name);
	}

	@Override
	public Type findType(Field field) {
		return ClassUtil.findGenericType(types, count, field);
	}

	@Override
	public <T> T decode(ReaderBuffer buf, Type type, boolean next, long options) {
		ICodec<T> codec = CodecFactory.getCodec(type);
		add(type);
		assert codec != null;
		if (next) {
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
		}
		try {
			return codec.decode(this, buf, type, options);
		} finally {
			poll();
		}
	}

	@Override
	public void addCompleteHook(Runnable hook) {
		hooks.add(hook);
	}

	@Override
	public void runCompleteHooks() {
		for (Runnable hook : hooks) {
			hook.run();
		}
	}

	@Override
	public void close(ReaderBuffer buf) {
		try {
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.EOF);
		} finally {
			hooks.clear();
			buf.close();
		}
	}
}