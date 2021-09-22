package com.keimons.deepjson.support.context;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
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