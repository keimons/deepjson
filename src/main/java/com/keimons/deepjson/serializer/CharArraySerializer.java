package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@code char[]}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class CharArraySerializer implements ISerializer {

	public static final CharArraySerializer instance = new CharArraySerializer();

	@Override
	public int length(Object object, long options) {
		char[] values = (char[]) object;
		int length = 2 + values.length * 2;
		for (char value : values) {
			length += SerializerUtil.length(value);
		}
		if (values.length > 1) {
			length += values.length - 1;
		}
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		char[] values = (char[]) object;
		for (char value : values) {
			if (SerializerUtil.coder(value) != 0) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
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