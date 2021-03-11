package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@code short[]}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ShortArraySerializer implements ISerializer {

	public static final ShortArraySerializer instance = new ShortArraySerializer();

	@Override
	public int length(Object object, long options) {
		short[] values = (short[]) object;
		int length = 2;
		for (short value : values) {
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
		short[] values = (short[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeInt(values[i]);
		}
		buf.writeMark(']');
	}
}