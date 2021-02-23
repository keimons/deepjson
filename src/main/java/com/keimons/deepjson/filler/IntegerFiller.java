package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class IntegerFiller extends BaseFiller {

	public IntegerFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	public int length(Object object, long options) {
		return SerializerUtil.size(unsafe.getInt(object, offset)) + size;
	}

	public int concat(Object object, ByteBuf buf) {
		int value = unsafe.getInt(object, offset);
		return buf.writeInt(this, value);
	}
}