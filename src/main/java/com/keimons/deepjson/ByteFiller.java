package com.keimons.deepjson;

import java.lang.reflect.Field;

public class ByteFiller extends BaseNumberFiller {

	public ByteFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	@Override
	public int length(Object object) {
		return DeepHelper.size(unsafe.getByte(object, offset)) + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		byte value = unsafe.getByte(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}