package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class FloatFiller extends BaseFiller {

	long valueOffset;
	long coderOffset;

	public FloatFiller(Class<?> clazz, Field field) throws NoSuchFieldException {
		super(clazz, field);
		valueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		coderOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
	}

	public int length(Object object, long options) {
		return size + String.valueOf(IFiller.unsafe.getFloat(object, offset)).length();
	}

	public int concat(Object object, ByteBuf buf) {
		float value = unsafe.getFloat(object, offset);
		return buf.writeStringWithNoMark(this, String.valueOf(value));
	}
}