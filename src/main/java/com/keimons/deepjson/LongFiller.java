package com.keimons.deepjson;

import java.lang.reflect.Field;

public class LongFiller extends BaseNumberFiller {

	public LongFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	public int length(Object object) {
		return DeepHelper.size(unsafe.getLong(object, offset)) + size;
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		long value = unsafe.getLong(object, offset);
		int length = DeepHelper.size(value);
		if (coder == DeepHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL + length;
			DeepHelper.putLATIN(code, writeIndex, value);
			code[writeIndex] = ',';
		} else {
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL + length;
			DeepHelper.putUTF16(code, writeIndex, value);
			writeIndex <<= 1;
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return length + size;
	}
}