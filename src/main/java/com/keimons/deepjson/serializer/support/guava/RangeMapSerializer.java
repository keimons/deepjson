package com.keimons.deepjson.serializer.support.guava;

import com.google.common.collect.RangeMap;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.serializer.ISerializer;

/**
 * Google Guava {@link RangeMap}序列化 (暂不支持)
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
@Deprecated
public class RangeMapSerializer implements ISerializer {

	@Override
	public int length(Object object, long options) {
		RangeMap<?, ?> value = (RangeMap<?, ?>) object;
		return 0;
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {

	}
}