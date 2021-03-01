package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
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

	/**
	 * 缓存原始字段信息
	 * <p>
	 * 因为缓存该字段会影响class的gc，在未来的版本中，该字段未来可能会被移除。
	 */
	private final Field field;

	/**
	 * 字段编码方式
	 * <p>
	 * 现在的java已经支持中文字段名称，所以该编码方式有可能是{@link SerializerUtil#UTF16}。
	 */
	private final byte coder;

	/**
	 * 字段长度
	 */
	private final int length;

	/**
	 * 字段名称 单字节存储
	 */
	private final byte[] fieldNameByLatin;

	/**
	 * 字段名称 双字节存储
	 */
	private final byte[] fieldNameByUtf16;

	/**
	 * 字段位置相对于对象首地址的偏移
	 */
	private final long offset;

	/**
	 * 构造字段信息
	 *
	 * @param field class中的字段
	 */
	public FieldInfo(Field field) {
		this.field = field;
		this.offset = unsafe.objectFieldOffset(field);
		String fieldName = "\"" + field.getName() + "\":";
		this.length = fieldName.length();
		byte[] byUtf16;
		byte[] byLatin = null;
		this.coder = unsafe.getByte(fieldName, SerializerUtil.CODER_OFFSET_STRING);
		byte[] bytes = (byte[]) unsafe.getObject(fieldName, SerializerUtil.VALUE_OFFSET_STRING);
		if (this.coder == SerializerUtil.LATIN) {
			byLatin = bytes;
			byUtf16 = new byte[this.length << 1];
			int writeIndex = 0;
			for (byte b : byLatin) {
				byUtf16[writeIndex++] = (byte) (b >> SerializerUtil.HI_BYTE_SHIFT);
				byUtf16[writeIndex++] = (byte) (b >> SerializerUtil.LO_BYTE_SHIFT);
			}
		} else {
			byUtf16 = bytes;
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
	public long offset() {
		return offset;
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