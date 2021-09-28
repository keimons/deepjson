package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.BaseCodec;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * {@link ParameterizedType}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class ParameterizedTypeCodec extends BaseCodec<Object> {

	public static final ParameterizedTypeCodec instance = new ParameterizedTypeCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		assert type instanceof ParameterizedType;
		ParameterizedType pt = (ParameterizedType) type;
		Type rawType = pt.getRawType();
		return context.decode(buf, rawType, false, options);
	}
}