package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ElementsFuture;
import com.keimons.deepjson.util.ArrayUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code Object[]}编解码器
 * <p>
 * 同时这也是一个{@link GenericArrayType}专用解码器。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ObjectArrayCodec extends AbstractArrayCodec<Object[]> {

	public static final ObjectArrayCodec instance = new ObjectArrayCodec();

	@Override
	public void build(WriterContext context, Object[] value) {
		context.cache(new ElementsFuture(value.length), EmptyCodec.instance);
		for (Object obj : value) {
			context.build(obj);
		}
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Object[] value, int uniqueId, long options) {
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		super.encode(context, buf, model, value, uniqueId, options);
	}

	@Override
	public void encode0(WriterContext context, WriterBuffer buf, Object[] values, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			context.encode(buf, CodecModel.V, options);
		}
	}

	public Object[] decode0(final ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
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
				values.add(context.decode(buf, componentType, options));
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		final Object[] result = ArrayUtil.newInstance(instanceType, values.size());
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
}