package com.keimons.deepjson;

import java.lang.reflect.Field;

public class ShortFiller extends BaseNumberFiller {

	public ShortFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	@Override
	public int length(Object object) {
		return DeepHelper.size(unsafe.getShort(object, offset)) + sizeL;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		short value = unsafe.getShort(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}