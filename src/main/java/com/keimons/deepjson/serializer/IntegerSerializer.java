package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;

/**
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class IntegerSerializer implements ISerializer {

	@Override
	public int length(Object object, long options) {
		int value = (int) object;
		return SerializerUtil.size(value);
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeInt((int) object);
		}
	}
}