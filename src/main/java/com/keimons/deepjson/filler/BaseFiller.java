package com.keimons.deepjson.filler;

import com.keimons.deepjson.util.SerializerUtil;

import java.lang.reflect.Field;

public abstract class BaseFiller implements IFiller {

	protected byte coder;

	protected int size;

	protected int sizeL;

	protected int sizeR = 1;

	protected byte[] utf16Key;

	protected byte[] latinKey;

	/**
	 * 字段偏移地址
	 */
	protected long offset;

	public BaseFiller(Class<?> clazz, Field field) {
		offset = unsafe.objectFieldOffset(field);
		String key = "\"" + field.getName() + "\":";
		initKey(key);
	}

	public void initKey(String context) {
		Field coder = null;
		try {
			coder = String.class.getDeclaredField("coder");
		} catch (NoSuchFieldException e) {
			// JDK9- ignore
		}
		Field value = null;
		try {
			value = String.class.getDeclaredField("value");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		this.coder = unsafe.getByte(context, unsafe.objectFieldOffset(coder));
		if (this.coder == SerializerUtil.LATIN) {
			this.latinKey = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.sizeL = latinKey.length;
			this.utf16Key = new byte[sizeL << 1];
			for (int i = 0; i < sizeL; i++) {
				SerializerUtil.putChar2(this.utf16Key, i, latinKey[i]);
			}
		} else {
			this.utf16Key = (byte[]) unsafe.getObject(context, unsafe.objectFieldOffset(value));
			this.sizeL = utf16Key.length >> 1;
		}
		size = sizeL + sizeR;
	}

	@Override
	public byte coder(Object object, long options) {
		return SerializerUtil.COMPACT_STRINGS ? coder : SerializerUtil.UTF16;
	}

	@Override
	public byte[] getFieldNameByUtf16() {
		return utf16Key;
	}

	@Override
	public byte[] getFieldNameByLatin() {
		return latinKey;
	}

	@Override
	public int length() {
		return size;
	}

	@Override
	public byte coder() {
		return coder;
	}

	@Override
	public long offset() {
		return 0;
	}
}