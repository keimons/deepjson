package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class BooleanFiller extends BaseFiller {

	public BooleanFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object, long options) {
		return (unsafe.getBoolean(object, offset) ? 4 : 5) + size;
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		boolean value = unsafe.getBoolean(object, offset);
		return buf.writeBoolean(this, value);
	}
}