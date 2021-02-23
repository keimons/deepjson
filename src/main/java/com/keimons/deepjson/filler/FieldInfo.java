package com.keimons.deepjson.filler;

import com.keimons.deepjson.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 字段信息
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class FieldInfo implements IFieldName {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final Field field;

	private final byte coder;

	private final int length;

	/**
	 * 字段名称
	 */
	private final byte[] fieldNameByLatin;

	private final byte[] fieldNameByUtf16;

	public FieldInfo(Field field) {
		this.field = field;
		String fieldName = "\"" + field.getName() + "\":";
		this.length = fieldName.length();
		Field coder = null;
		Field value = null;
		try {
			coder = String.class.getDeclaredField("coder");
			value = String.class.getDeclaredField("value");
		} catch (NoSuchFieldException e) {
			// JDK9- ignore
		}
		byte[] byUtf16;
		byte[] byLatin = null;
		this.coder = unsafe.getByte(fieldName, unsafe.objectFieldOffset(coder));
		if (this.coder == SerializerUtil.LATIN) {
			byLatin = (byte[]) unsafe.getObject(fieldName, unsafe.objectFieldOffset(value));
			byUtf16 = new byte[this.length << 1];
			for (int i = 0; i < byLatin.length; i++) {
				SerializerUtil.putChar2(byUtf16, i, byLatin[i]);
			}
		} else {
			byUtf16 = (byte[]) unsafe.getObject(fieldName, unsafe.objectFieldOffset(value));
		}
		this.fieldNameByUtf16 = byUtf16;
		this.fieldNameByLatin = byLatin;
	}

	public Field getField() {
		return field;
	}

	public byte coder() {
		return coder;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public byte[] getFieldNameByUtf16() {
		return fieldNameByUtf16;
	}

	@Override
	public byte[] getFieldNameByLatin() {
		return fieldNameByLatin;
	}
}