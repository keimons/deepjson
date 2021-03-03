package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 字段信息
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class FieldInfo {

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
	 * 字段位置相对于对象首地址的偏移
	 */
	private final long offset;

	/**
	 * 字段名称
	 */
	private final String fieldName;

	/**
	 * 构造字段信息
	 *
	 * @param field class中的字段
	 */
	public FieldInfo(Field field) {
		this.field = field;
		this.offset = unsafe.objectFieldOffset(field);
		this.fieldName = "\"" + field.getName() + "\":";
		if (PlatformUtil.javaVersion() >= 9) {
			this.coder = unsafe.getByte(this.fieldName, SerializerUtil.CODER_OFFSET_STRING);
		} else {
			this.coder = 1;
		}
	}

	public Field getField() {
		return field;
	}

	public byte coder() {
		return coder;
	}

	public long offset() {
		return offset;
	}

	public int length() {
		return fieldName.length();
	}

	public String getFieldName() {
		return fieldName;
	}
}