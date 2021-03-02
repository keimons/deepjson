package com.keimons.deepjson.serializer;

import jdk.internal.vm.annotation.ForceInline;

/**
 * 字段名称信息
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
// class and all method is always final
public final class FieldName implements IFieldName {

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
	 * 字段名称 char存储
	 */
	private final char[] fieldNameByChar;

	public FieldName(byte[] $latin, byte[] $utf16, char[] $char) {
		this.fieldNameByLatin = $latin;
		this.fieldNameByUtf16 = $utf16;
		this.fieldNameByChar = $char;
		// $char always has value
		this.length = $char.length;
	}

	@ForceInline
	@Override
	public final int length() {
		return length;
	}

	@ForceInline
	@Override
	public final byte[] getFieldNameByUtf16() {
		return fieldNameByUtf16;
	}

	@ForceInline
	@Override
	public final byte[] getFieldNameByLatin() {
		return fieldNameByLatin;
	}

	@ForceInline
	@Override
	public final char[] getFieldNameByChar() {
		return fieldNameByChar;
	}
}