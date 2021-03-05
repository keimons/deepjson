package com.keimons.deepjson.serializer;

/**
 * char[]序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class CharArraySerializer implements ISerializer {

	public static final CharArraySerializer instance = new CharArraySerializer();

	@Override
	public int length(Object object, long options) {
		char[] values = (char[]) object;
		int length = 2 + values.length * 3;
		if (values.length > 1) {
			length += values.length - 1;
		}
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		char[] values = (char[]) object;
		for (char value : values) {
			if (value >>> 8 != 0) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
			return;
		}
		buf.writeMark('[');
		char[] values = (char[]) object;
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeChar(values[i]);
		}
		buf.writeMark(']');
	}
}