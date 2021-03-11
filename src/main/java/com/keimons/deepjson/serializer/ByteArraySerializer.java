package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@code byte[]}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ByteArraySerializer implements ISerializer {

	public static final ByteArraySerializer instance = new ByteArraySerializer();

	@Override
	public int length(Object object, long options) {
		byte[] values = (byte[]) object;
		int length = 2;
		for (byte value : values) {
			length += SerializerUtil.length(value);
		}
		if (values.length > 1) {
			length += values.length - 1;
		}
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
			return;
		}
		buf.writeMark('[');
		byte[] values = (byte[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeInt(values[i]);
		}
		buf.writeMark(']');
	}
}