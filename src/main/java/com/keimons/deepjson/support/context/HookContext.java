package com.keimons.deepjson.support.context;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.internal.util.GenericUtil;
import com.keimons.deepjson.internal.util.Stack;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.util.TypeNotFoundException;
import com.keimons.deepjson.util.UnsafeUtil;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下文环境
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class HookContext extends ReaderContext {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	Map<Integer, Object> context = new HashMap<Integer, Object>();

	/**
	 * 钩子函数
	 * <p>
	 * 解码完成后，在处理一些东西时，需要使用钩子函数。钩子函数配合
	 */
	List<Runnable> hooks = new ArrayList<Runnable>();

	Stack<Type> types = new Stack<Type>();

	@Override
	public void put(int uniqueId, Object value) {
		context.put(uniqueId, value);
	}

	@Override
	public Object get(int uniqueId) {
		return context.get(uniqueId);
	}

	@Override
	public Class<?> findInstanceType(Type type, Class<?> excepted) {
		return GenericUtil.findClass(types, type, excepted);
	}

	@Override
	public @NotNull Type findInstanceType(final TypeVariable<?> type) {
		Class<?> clazz = (Class<?>) type.getGenericDeclaration();
		String name = type.getName();
		Type result = GenericUtil.findGenericType(types, clazz, name);
		return result == null ? type : result;
	}

	@Override
	public Type findType(Class<?> target, String name) {
		Type result = GenericUtil.findGenericType(types, target, name);
		if (result == null) {
			String msg = "the '" + name + "' of " + target.getName() + " cannot be found.";
			throw new TypeNotFoundException(msg);
		}
		return result;
	}

	@Override
	public <T> T decode(JsonReader reader, Type type, long options) {
		ICodec<T> codec = CodecFactory.getCodec(type);
		assert codec != null;
		boolean cacheType = codec.isCacheType();
		try {
			if (cacheType) {
				types.push(type);
			}
			return codec.decode(this, reader, type, options);
		} finally {
			if (cacheType) {
				types.poll();
			}
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
	public void addCompleteHook(Object instance, MethodHandle setter, int uniqueId) {
		final Map<Integer, Object> context = this.context;
		addCompleteHook(new Runnable() {
			@Override
			public void run() {
				Object value = context.get(uniqueId);
				try {
					setter.invoke(instance, value);
				} catch (Throwable e) {
					e.printStackTrace();
				}
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
	public void close(JsonReader reader) {
		try {
			reader.nextToken();
			reader.assertExpectedSyntax(SyntaxToken.EOF);
		} finally {
			hooks.clear();
			reader.close();
		}
	}
}