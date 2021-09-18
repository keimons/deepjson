package com.keimons.deepjson.compiler;

import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 字段信息
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
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
	 * 字段位置相对于对象首地址的偏移
	 */
	private final long offset;

	/**
	 * 字段名称
	 */
	private final String fieldName;

	private final String writeName;

	/**
	 * 构造字段信息
	 *
	 * @param field class中的字段
	 */
	public FieldInfo(Field field) {
		this.field = field;
		this.offset = unsafe.objectFieldOffset(field);
		this.fieldName = field.getName();
		this.writeName = field.getName();
	}

	public Field getField() {
		return field;
	}

	public long offset() {
		return offset;
	}

	public Class<?> getFieldType() {
		return field.getType();
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getWriteName() {
		return writeName;
	}
}