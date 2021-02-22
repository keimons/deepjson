package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ByteBuf;

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
	public byte coder(Object object, long options) {
		String value = (String) unsafe.getObject(object, offset);
		if (value == null) {
			return FillerHelper.LATIN;
		} else {
			return unsafe.getByte(value, OFFSET_CODER);
		}
	}

	@Override
	public int length(Object object, long options) {
		String value = (String) unsafe.getObject(object, offset);
		if (value == null) {
			return 0;
		} else {
			return size + 2 + value.length();
		}
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		String value = (String) unsafe.getObject(object, offset);
		return buf.writeString(this, value);
	}
}