package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class IntegerFiller extends BaseFiller {

	public IntegerFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	public int length(Object object, long options) {
		return FillerHelper.size(unsafe.getInt(object, offset)) + size;
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex, long options) {
		int value = unsafe.getInt(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}