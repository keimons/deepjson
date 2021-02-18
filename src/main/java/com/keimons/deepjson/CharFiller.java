package com.keimons.deepjson;

import java.lang.reflect.Field;

public class CharFiller extends BaseNumberFiller {

	public CharFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	@Override
	public byte coder(Object object) {
		return (byte) (coder | (unsafe.getChar(object, offset) >>> 8 == 0 ? 0 : 1));
	}

	@Override
	public int length(Object object) {
		return 1 + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		char value = unsafe.getChar(object, offset);
		if (coder == DeepHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL;
			DeepHelper.putChar1(code, writeIndex++, value);
			code[writeIndex] = ',';
		} else {
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL;
			DeepHelper.putChar2(code, writeIndex++, value);
			writeIndex <<= 1;
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return 1 + size;
	}
}