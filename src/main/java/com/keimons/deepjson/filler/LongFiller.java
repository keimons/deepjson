package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class LongFiller extends BaseFiller {

	public LongFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	public int length(Object object, long options) {
		return FillerHelper.size(unsafe.getLong(object, offset)) + size;
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex, long options) {
		long value = unsafe.getLong(object, offset);
		int length = FillerHelper.size(value);
		if (coder == FillerHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL + length;
			FillerHelper.putLATIN(code, writeIndex, value);
			code[writeIndex] = ',';
		} else {
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL + length;
			FillerHelper.putUTF16(code, writeIndex, value);
			writeIndex <<= 1;
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return length + size;
	}
}