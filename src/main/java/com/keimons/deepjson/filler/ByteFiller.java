package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class ByteFiller extends BaseFiller {

	public ByteFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object) {
		return FillerHelper.size(unsafe.getByte(object, offset)) + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		byte value = unsafe.getByte(object, offset);
		return concat(code, coder, writeIndex, value);
	}
}