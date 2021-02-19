package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class StringFiller extends BaseFiller {

	private static final long OFFSET_CODER;
	private static final long OFFSET_VALUE;

	static {
		long coderOffset = 0;
		long valueOffset = 0;
		try {
			coderOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
			valueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		OFFSET_CODER = coderOffset;
		OFFSET_VALUE = valueOffset;
	}

	public StringFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public byte coder(Object object) {
		String value = (String) unsafe.getObject(object, offset);
		if (value == null) {
			return FillerHelper.LATIN;
		} else {
			return unsafe.getByte(value, OFFSET_CODER);
		}
	}

	@Override
	public int length(Object object) {
		String value = (String) unsafe.getObject(object, offset);
		if (value == null) {
			return 0;
		} else {
			return size + 2 + value.length();
		}
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		String value = (String) unsafe.getObject(object, offset);
		byte[] bytes = (byte[]) unsafe.getObject(value, OFFSET_VALUE);
		if (coder == FillerHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL;
			code[writeIndex++] = '"';
			System.arraycopy(bytes, 0, code, writeIndex, bytes.length);
			writeIndex += bytes.length;
			code[writeIndex++] = '"';
			code[writeIndex] = ',';
		} else {
			// 写入key
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL;
			int index = writeIndex << 1;
			code[index++] = UTF16_QUOTATION[0];
			code[index] = UTF16_QUOTATION[1];
			writeIndex++;
			byte valueCoder = unsafe.getByte(value, OFFSET_CODER);
			if (valueCoder == FillerHelper.LATIN) {
				writeIndex <<= 1;
				for (int i = 0; i < bytes.length; i++) {
					byte b = bytes[i];
					code[writeIndex++] = (byte) (b >> FillerHelper.HI_BYTE_SHIFT);
					code[writeIndex++] = (byte) (b >> FillerHelper.LO_BYTE_SHIFT);
				}
				code[writeIndex++] = UTF16_QUOTATION[0];
				code[writeIndex++] = UTF16_QUOTATION[1];
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
			} else {
				System.arraycopy(bytes, 0, code, writeIndex << coder, bytes.length);
				writeIndex += value.length();
				writeIndex <<= 1;
				code[writeIndex++] = UTF16_QUOTATION[0];
				code[writeIndex++] = UTF16_QUOTATION[1];
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
			}
		}
		return value.length() + 2 + size;
	}
}