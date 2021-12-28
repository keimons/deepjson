package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.support.CodecFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * {@link ParameterizedType}参数化类型编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class ParameterizedTypeCodec extends PhantomCodec {

	public static final ParameterizedTypeCodec instance = new ParameterizedTypeCodec();

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public Object decode(ReaderContext context, JsonReader reader, Type type, long options) {
		assert type instanceof ParameterizedType;
		ParameterizedType pt = (ParameterizedType) type;
		Class<?> clazz = (Class<?>) pt.getRawType();
		ICodec<?> codec = CodecFactory.getCodec(clazz);
		assert codec != null;
		return codec.decode(context, reader, clazz, options);
	}
}