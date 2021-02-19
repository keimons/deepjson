package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class CharFiller extends BaseFiller {

	public CharFiller(Class<?> clazz, Field field) {
		super(clazz, field);
		String key = "\"" + field.getName() + "\":\"";
		initKey(key);
	}

	@Override
	public byte coder(Object object) {
		if (FillerHelper.COMPACT_STRINGS) {
			int i = unsafe.getChar(object, offset) >>> 8;
			return (byte) (coder | (i == 0 ? FillerHelper.LATIN : FillerHelper.UTF16));
		} else {
			return FillerHelper.UTF16;
		}
	}

	@Override
	public int length(Object object) {
		return 2 + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		char value = unsafe.getChar(object, offset);
		if (coder == FillerHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL;
			FillerHelper.putChar1(code, writeIndex++, value);
			code[writeIndex++] = '"';
			code[writeIndex] = ',';
		} else {
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL;
			FillerHelper.putChar2(code, writeIndex++, value);
			writeIndex <<= 1;
			code[writeIndex++] = UTF16_QUOTATION[0];
			code[writeIndex++] = UTF16_QUOTATION[1];
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return 2 + size;
	}
}