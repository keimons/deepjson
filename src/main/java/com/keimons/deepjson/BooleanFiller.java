package com.keimons.deepjson;

import java.lang.reflect.Field;

public class BooleanFiller extends BaseNumberFiller {

	private static final byte[] LATIN_TRUE = {'t', 'r', 'u', 'e'};
	private static final byte[] LATIN_FALSE = {'f', 'a', 'l', 's', 'e'};

	private static final byte[] UTF16_TRUE = {
			(byte) ('t' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('t' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('r' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('r' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('u' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('u' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('e' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('e' >> DeepHelper.LO_BYTE_SHIFT)
	};
	private static final byte[] UTF16_FALSE = {
			(byte) ('f' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('f' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('a' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('a' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('l' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('l' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('s' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('s' >> DeepHelper.LO_BYTE_SHIFT),
			(byte) ('e' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) ('e' >> DeepHelper.LO_BYTE_SHIFT)
	};

	public BooleanFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
	}

	@Override
	public int length(Object object) {
		return (unsafe.getBoolean(object, offset) ? 4 : 5) + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		writeIndex <<= coder;
		boolean value = unsafe.getBoolean(object, offset);
		if (coder == DeepHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL;
			if (value) {
				code[writeIndex++] = 't';
				code[writeIndex++] = 'r';
				code[writeIndex++] = 'u';
				code[writeIndex++] = 'e';
				code[writeIndex] = ',';
				return 4 + size;
			} else {
				code[writeIndex++] = 'f';
				code[writeIndex++] = 'a';
				code[writeIndex++] = 'l';
				code[writeIndex++] = 's';
				code[writeIndex] = 'e';
				code[writeIndex] = ',';
				return 5 + size;
			}
		} else {
			System.arraycopy(value1, 0, code, writeIndex, sizeL << coder);
			writeIndex += sizeL << coder;
			if (value) {
				code[writeIndex++] = UTF16_TRUE[0];
				code[writeIndex++] = UTF16_TRUE[1];
				code[writeIndex++] = UTF16_TRUE[2];
				code[writeIndex++] = UTF16_TRUE[3];
				code[writeIndex++] = UTF16_TRUE[4];
				code[writeIndex++] = UTF16_TRUE[5];
				code[writeIndex++] = UTF16_TRUE[6];
				code[writeIndex++] = UTF16_TRUE[7];
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
				return 4 + size;
			} else {
				code[writeIndex++] = UTF16_FALSE[0];
				code[writeIndex++] = UTF16_FALSE[1];
				code[writeIndex++] = UTF16_FALSE[2];
				code[writeIndex++] = UTF16_FALSE[3];
				code[writeIndex++] = UTF16_FALSE[4];
				code[writeIndex++] = UTF16_FALSE[5];
				code[writeIndex++] = UTF16_FALSE[6];
				code[writeIndex++] = UTF16_FALSE[7];
				code[writeIndex++] = UTF16_FALSE[8];
				code[writeIndex] = UTF16_FALSE[9];
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
				return 5 + size;
			}
		}
	}
}