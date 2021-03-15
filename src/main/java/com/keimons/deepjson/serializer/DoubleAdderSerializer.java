package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.RyuDouble;

import java.util.concurrent.atomic.DoubleAdder;

/**
 * {@link DoubleAdder}序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class DoubleAdderSerializer implements ISerializer {

	public static final DoubleAdderSerializer instance = new DoubleAdderSerializer();

	@Override
	public int length(Object object, long options) {
		DoubleAdder value = (DoubleAdder) object;
		return RyuDouble.length(value.sum());
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		DoubleAdder value = (DoubleAdder) object;
		buf.writeDouble(value.sum());
	}
}