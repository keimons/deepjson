package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.RyuFloat;

/**
 * float[]序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class FloatArraySerializer implements ISerializer {

	public static final FloatArraySerializer instance = new FloatArraySerializer();

	@Override
	public int length(Object object, long options) {
		float[] values = (float[]) object;
		int length = 2;
		for (float value : values) {
			length += RyuFloat.length(value);
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
		float[] values = (float[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeFloat(values[i]);
		}
		buf.writeMark(']');
	}
}