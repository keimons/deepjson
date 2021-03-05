package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.RyuDouble;

/**
 * double[]序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class DoubleArraySerializer implements ISerializer {

	public static final DoubleArraySerializer instance = new DoubleArraySerializer();

	@Override
	public int length(Object object, long options) {
		double[] values = (double[]) object;
		int length = 2;
		for (double value : values) {
			length += RyuDouble.length(value);
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
		double[] values = (double[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeDouble(values[i]);
		}
		buf.writeMark(']');
	}
}