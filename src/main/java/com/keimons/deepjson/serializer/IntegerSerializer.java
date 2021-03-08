package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * int类型序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class IntegerSerializer implements ISerializer {

	public static final IntegerSerializer instance = new IntegerSerializer();

	@Override
	public int length(Object object, long options) {
		int value = (int) object;
		return SerializerUtil.length(value);
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