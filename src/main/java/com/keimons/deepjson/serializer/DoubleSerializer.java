package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.RyuDouble;

/**
 * {@link Double}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class DoubleSerializer implements ISerializer {

	public static final DoubleSerializer instance = new DoubleSerializer();

	@Override
	public int length(Object object, long options) {
		return RyuDouble.length((double) object);
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeDouble((double) object);
		}
	}
}