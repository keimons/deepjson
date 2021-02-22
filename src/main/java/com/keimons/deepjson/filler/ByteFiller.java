package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class ByteFiller extends BaseFiller {

	public ByteFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object, long options) {
		return FillerHelper.size(unsafe.getByte(object, offset)) + size;
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		byte value = unsafe.getByte(object, offset);
		return buf.writeInt(this, value);
	}
}