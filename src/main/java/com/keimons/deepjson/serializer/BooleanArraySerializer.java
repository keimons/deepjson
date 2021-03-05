package com.keimons.deepjson.serializer;

/**
 * boolean[]序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class BooleanArraySerializer implements ISerializer {

	public static final BooleanArraySerializer instance = new BooleanArraySerializer();

	@Override
	public int length(Object object, long options) {
		boolean[] values = (boolean[]) object;
		int length = 2;
		for (boolean value : values) {
			length += value ? 4 : 5;
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
		boolean[] values = (boolean[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeBoolean(values[i]);
		}
		buf.writeMark(']');
	}
}