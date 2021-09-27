package com.keimons.deepjson.support.context;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

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

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	Map<Integer, Object> context = new HashMap<Integer, Object>();

	/**
	 * 钩子函数
	 * <p>
	 * 解码完成后，在处理一些东西时，需要使用钩子函数。钩子函数配合
	 */
	List<Runnable> hooks = new ArrayList<Runnable>();

	/**
	 * 依赖于深度优先的栈
	 */
	Type[] types = new Type[32];

	/**
	 * 当前正在写入的位置
	 */
	int writerIndex;

	/**
	 * 增加一个类型
	 *
	 * @param type 类型
	 */
	private void add(Type type) {
		if (writerIndex >= types.length) {
			types = Arrays.copyOf(types, types.length << 1);
		}
		types[writerIndex++] = type;
	}

	/**
	 * 移除一个类型
	 */
	private void poll() {
		types[--writerIndex] = null;
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
		return ClassUtil.findClass(types, writerIndex, type);
	}

	@Override
	public Type findType(Class<?> target, String name) {
		return ClassUtil.findGenericType(types, writerIndex, target, name);
	}

	@Override
	public Type findType(Field field) {
		Type type = field.getGenericType();
		return ClassUtil.findType(types, writerIndex, type);
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
	public void addCompleteHook(final Object instance, long offset, final int uniqueId) {
		final Map<Integer, Object> context = this.context;
		addCompleteHook(new Runnable() {
			@Override
			public void run() {
				Object value = context.get(uniqueId);
				unsafe.putObject(instance, 12L, value);
			}
		});
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