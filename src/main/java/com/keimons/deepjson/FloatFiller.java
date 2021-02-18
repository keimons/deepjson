package com.keimons.deepjson;

import java.lang.reflect.Field;

public class FloatFiller extends BaseNumberFiller {

	public FloatFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	public int length(Object object) {
		try {
			return String.valueOf(IFiller.unsafe.getFloat(object, offset)).length();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 1;
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		try {
			byte[] string = String.valueOf(unsafe.getFloat(object, offset)).getBytes();
			System.arraycopy(string, 0, code, writeIndex, string.length);
			return string.length;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 1;
	}
}