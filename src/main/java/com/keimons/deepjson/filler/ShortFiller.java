package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class ShortFiller extends BaseFiller {

	public ShortFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object, long options) {
		return SerializerUtil.size(unsafe.getShort(object, offset)) + size;
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		short value = unsafe.getShort(object, offset);
		return buf.writeInt(this, value);
	}
}