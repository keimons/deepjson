package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class BooleanFiller extends BaseFiller {

	private static final byte[] UTF16_TRUE = {
			(byte) ('t' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('t' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('r' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('r' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('u' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('u' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('e' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('e' >> FillerHelper.LO_BYTE_SHIFT)
	};
	private static final byte[] UTF16_FALSE = {
			(byte) ('f' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('f' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('a' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('a' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('l' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('l' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('s' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('s' >> FillerHelper.LO_BYTE_SHIFT),
			(byte) ('e' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('e' >> FillerHelper.LO_BYTE_SHIFT)
	};

	public BooleanFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public int length(Object object, long options) {
		return (unsafe.getBoolean(object, offset) ? 4 : 5) + size;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex, long options) {
		writeIndex <<= coder;
		boolean value = unsafe.getBoolean(object, offset);
		if (coder == FillerHelper.LATIN) {
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
				code[writeIndex++] = 'e';
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
				code[writeIndex++] = UTF16_FALSE[9];
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
				return 5 + size;
			}
		}
	}
}