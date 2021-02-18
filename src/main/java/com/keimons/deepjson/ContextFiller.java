package com.keimons.deepjson;

import java.lang.reflect.Field;

public class ContextFiller implements IFiller {

	byte coder;

	int size0;

	int size1;

	byte[] value0;

	byte[] value1;

	public ContextFiller(String context) throws NoSuchFieldException {
		Field coder = String.class.getDeclaredField("coder");
		Field value = String.class.getDeclaredField("value");
		this.coder = unsafe.getByte(context, unsafe.objectFieldOffset(coder));
		if (this.coder == DeepHelper.LATIN) {
			this.value0 = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.size0 = value0.length;
			this.value1 = new byte[size0 << 1];
			for (int i = 0; i < size0; i++) {
				DeepHelper.putChar2(this.value1, i, value0[i]);
			}
			this.size1 = size0;
		} else {
			this.value1 = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.size1 = value1.length >> 1;
		}
	}

	public int length(Object object) {
		if (coder == 0) {
			return size0;
		} else {
			return size1;
		}
	}

	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		writeIndex <<= coder;
		if (coder == DeepHelper.LATIN) {
			System.arraycopy(value0, 0, code, writeIndex, size0);
			return size0;
		} else {
			System.arraycopy(value1, 0, code, writeIndex, size1 << coder);
			return size1;
		}
	}
}