package com.keimons.deepjson.filler;

import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.serializer.ByteBuf;
import sun.misc.Unsafe;

/**
 * 填充数据
 */
public interface IFiller extends IFieldName {

	/**
	 * 逗号
	 */
	byte[] UTF16_SPLIT = {
			(byte) (',' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) (',' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	/**
	 * 双引号
	 */
	byte[] UTF16_QUOTATION = {
			(byte) ('"' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('"' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	/**
	 * 中括号左
	 */
	byte[] UTF16_BRACKET_L = {
			(byte) ('[' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('[' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	/**
	 * 中括号右
	 */
	byte[] UTF16_BRACKET_R = {
			(byte) (']' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) (']' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	default byte coder(Object object, long options) {
		return (byte) (SerializerUtil.COMPACT_STRINGS ? 0 : 1);
	}

	int length(Object object, long options);

	int concat(Object object, ByteBuf buf);
}