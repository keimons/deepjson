package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class StringFiller extends BaseFiller {

	long coderOffset;
	long valueOffset;

	public StringFiller(Class<?> clazz, Field field) {
		super(clazz, field);
		try {
			coderOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
			valueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int length(Object object) {
		String str = (String) unsafe.getObject(object, offset);
		int length = str.getBytes().length;
		byte coder = (byte) unsafe.getObject(str, coderOffset);
		byte[] value = (byte[]) unsafe.getObject(str, valueOffset);
		return value.length << coder;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		return 0;
	}
}