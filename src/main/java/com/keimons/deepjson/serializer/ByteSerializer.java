package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;

/**
 * byte类型序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ByteSerializer implements ISerializer {

	public static final ByteSerializer instance = new ByteSerializer();

	@Override
	public int length(Object object, long options) {
		byte value = (byte) object;
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
			buf.writeInt((byte) object);
		}
	}
}