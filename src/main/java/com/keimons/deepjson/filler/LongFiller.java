package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class LongFiller extends BaseFiller {

	public LongFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	public int length(Object object, long options) {
		return FillerHelper.size(unsafe.getLong(object, offset)) + size;
	}

	public int concat(Object object, ByteBuf buf) {
		long value = unsafe.getLong(object, offset);
		return buf.writeLong(this, value);
	}
}