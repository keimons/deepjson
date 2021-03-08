package com.keimons.deepjson.compiler;

import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

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
	 * Unsafe操作类
	 */
	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

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

	public FieldName(String fieldName) {
		byte[] byUtf16 = null;
		byte[] byLatin = null;

		Object object = unsafe.getObject(fieldName, SerializerUtil.VALUE_OFFSET_STRING);
		// less jdk 1.8 it will be char[]
		if (object instanceof byte[]) {
			byte[] bytes = (byte[]) object;
			byte coder = unsafe.getByte(fieldName, SerializerUtil.CODER_OFFSET_STRING);
			if (coder == SerializerUtil.LATIN) {
				byLatin = new byte[bytes.length];
				System.arraycopy(bytes, 0, byLatin, 0, bytes.length);
				byUtf16 = new byte[bytes.length << 1];
				int writeIndex = 0;
				for (byte b : byLatin) {
					byUtf16[writeIndex++] = (byte) (b >> SerializerUtil.HI_BYTE_SHIFT);
					byUtf16[writeIndex++] = (byte) (b >> SerializerUtil.LO_BYTE_SHIFT);
				}
			} else {
				byUtf16 = new byte[bytes.length];
				System.arraycopy(bytes, 0, byUtf16, 0, bytes.length);
			}
		}
		this.length = fieldName.length();
		this.fieldNameByChar = fieldName.toCharArray();
		this.fieldNameByUtf16 = byUtf16;
		this.fieldNameByLatin = byLatin;
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