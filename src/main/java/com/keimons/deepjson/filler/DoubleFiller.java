package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

import java.lang.reflect.Field;

public class DoubleFiller extends BaseFiller {

	public DoubleFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object, long options) {
		return size + String.valueOf(IFiller.unsafe.getDouble(object, offset)).length();
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		double value = unsafe.getDouble(object, offset);
		return buf.writeStringWithNoMark(this, String.valueOf(value));
	}
}