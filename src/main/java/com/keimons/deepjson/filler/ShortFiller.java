package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class ShortFiller extends BaseFiller {

	public ShortFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object) {
		return FillerHelper.size(unsafe.getShort(object, offset)) + sizeL;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		short value = unsafe.getShort(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}