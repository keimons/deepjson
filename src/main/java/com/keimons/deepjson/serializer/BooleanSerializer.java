package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

/**
 * {@link Boolean}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class BooleanSerializer implements ISerializer {

	public static final BooleanSerializer instance = new BooleanSerializer();

	@Override
	public int length(Object object, long options) {
		boolean value = (boolean) object;
		return value ? 4 : 5;
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
			buf.writeBoolean((boolean) object);
		}
	}
}