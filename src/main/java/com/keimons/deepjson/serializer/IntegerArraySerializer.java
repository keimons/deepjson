package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * int[]序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class IntegerArraySerializer implements ISerializer {

	public static final IntegerArraySerializer instance = new IntegerArraySerializer();

	@Override
	public int length(Object object, long options) {
		int[] values = (int[]) object;
		int length = 2;
		for (int value : values) {
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
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
			return;
		}
		buf.writeMark('[');
		int[] values = (int[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeInt(values[i]);
		}
		buf.writeMark(']');
	}
}