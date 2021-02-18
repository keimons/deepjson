package com.keimons.deepjson;

import java.lang.reflect.Field;

public class IntegerFiller extends BaseNumberFiller {

	public IntegerFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	public int length(Object object) {
		return DeepHelper.size(unsafe.getInt(object, offset)) + size;
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		int value = unsafe.getInt(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}