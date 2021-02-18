package com.keimons.deepjson;

import java.lang.reflect.Field;

public abstract class BaseNumberFiller implements IFiller {

	public static final String STRING_UTF_1 = "java.lang.StringLatin1";
	public static final String STRING_UTF_2 = "java.lang.StringUTF16";

	protected byte coder;

	protected int size;

	protected int sizeL;

	protected int sizeR = 1;

	protected byte[] value0;

	protected byte[] value1;

	/**
	 * 字段偏移地址
	 */
	protected long offset;

	public BaseNumberFiller(Class<?> clazz, Field field) {
		offset = unsafe.objectFieldOffset(field);
		String key = "\"" + field.getName() + "\":";
		initKey(key);
	}

	public void initKey(String context) {
		Field coder = null;
		try {
			coder = String.class.getDeclaredField("coder");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		Field value = null;
		try {
			value = String.class.getDeclaredField("value");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		this.coder = unsafe.getByte(context, unsafe.objectFieldOffset(coder));
		if (this.coder == DeepHelper.LATIN) {
			this.value0 = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.sizeL = value0.length;
			this.value1 = new byte[sizeL << 1];
			for (int i = 0; i < sizeL; i++) {
				DeepHelper.putChar2(this.value1, i, value0[i]);
			}
		} else {
			this.value1 = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.sizeL = value1.length >> 1;
		}
		size = sizeL + sizeR;
	}

	public int concat(byte[] code, byte coder, int writeIndex, int value) {
		int length = DeepHelper.size(value);
		if (coder == DeepHelper.LATIN) {
			for (byte b : value0) {
				code[writeIndex++] = b;
			}
			writeIndex += length;
			DeepHelper.putLATIN(code, writeIndex, value);
			code[writeIndex] = ',';
		} else {
			int index = writeIndex << 1;
			for (byte b : value1) {
				code[index++] = b;
			}
			writeIndex += sizeL + length;
			DeepHelper.putUTF16(code, writeIndex, value);
			writeIndex <<= 1;
			code[writeIndex++] = UTF16_SPLIT[0];
			code[writeIndex] = UTF16_SPLIT[1];
		}
		return length + size;
	}

	@Override
	public byte coder(Object object) {
		return coder;
	}
}