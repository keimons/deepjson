package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

import java.util.concurrent.atomic.LongAdder;

/**
 * {@link LongAdder}序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class LongAdderSerializer implements ISerializer {

	public static final LongAdderSerializer instance = new LongAdderSerializer();

	@Override
	public int length(Object object, long options) {
		LongAdder value = (LongAdder) object;
		return SerializerUtil.length(value.sum());
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		LongAdder value = (LongAdder) object;
		buf.writeLong(value.sum());
	}
}