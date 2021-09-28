package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.BaseCodec;

import java.lang.reflect.Type;

/**
 * {@link Class}编解码器
 * <p>
 * 这个编解码器不会被用到，否则将触发死循环。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class ClassCodec extends BaseCodec<Object> {

	private static final ClassCodec instance = new ClassCodec();

	private ClassCodec() {

	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		throw new UnsupportedOperationException();
	}
}