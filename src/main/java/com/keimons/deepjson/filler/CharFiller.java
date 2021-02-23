package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class CharFiller extends BaseFiller {

	public CharFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public byte coder(Object object, long options) {
		if (SerializerUtil.COMPACT_STRINGS) {
			int i = unsafe.getChar(object, offset) >>> 8;
			return (byte) (coder | (i == 0 ? SerializerUtil.LATIN : SerializerUtil.UTF16));
		} else {
			return SerializerUtil.UTF16;
		}
	}

	@Override
	public int length(Object object, long options) {
		return 3 + size;
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		char value = unsafe.getChar(object, offset);
		return buf.writeChar(this, value);
	}
}