package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class FloatFiller extends BaseFiller {

	long valueOffset;
	long coderOffset;

	public FloatFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		super(clazz, field);
		valueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		coderOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
	}

	public int length(Object object) {
		return size + String.valueOf(IFiller.unsafe.getFloat(object, offset)).length();
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		String value = String.valueOf(unsafe.getFloat(object, offset));
		byte[] bytes = (byte[]) unsafe.getObject(value, valueOffset);
		if (coder == FillerHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, sizeL);
			writeIndex += sizeL;
			int length = bytes.length;
			System.arraycopy(bytes, 0, code, writeIndex, length);
			writeIndex += length;
			code[writeIndex] = ',';
		} else {
			System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
			writeIndex += sizeL;

			byte valueCoder = unsafe.getByte(value, coderOffset);
			if (valueCoder == FillerHelper.LATIN) {
				writeIndex <<= 1;
				for (int i = 0; i < bytes.length; i++) {
					byte b = bytes[i];
					code[writeIndex++] = (byte) (b >> FillerHelper.HI_BYTE_SHIFT);
					code[writeIndex++] = (byte) (b >> FillerHelper.LO_BYTE_SHIFT);
				}
			} else {
				System.arraycopy(bytes, 0, code, writeIndex << 1, bytes.length);
				writeIndex += value.length();
				writeIndex <<= 1;
			}
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return size + value.length();
	}
}