package com.keimons.deepjson.filler;

import com.keimons.deepjson.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * 填充数据
 */
public interface IFiller {

	/**
	 * 逗号
	 */
	byte[] UTF16_SPLIT = {
			(byte) (',' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) (',' >> FillerHelper.LO_BYTE_SHIFT),
	};

	/**
	 * 双引号
	 */
	byte[] UTF16_QUOTATION = {
			(byte) ('"' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('"' >> FillerHelper.LO_BYTE_SHIFT),
	};

	/**
	 * 中括号左
	 */
	byte[] UTF16_BRACKET_L = {
			(byte) ('[' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('[' >> FillerHelper.LO_BYTE_SHIFT),
	};

	/**
	 * 中括号右
	 */
	byte[] UTF16_BRACKET_R = {
			(byte) (']' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) (']' >> FillerHelper.LO_BYTE_SHIFT),
	};

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	default byte coder(Object object) {
		return (byte) (FillerHelper.COMPACT_STRINGS ? 0 : 1);
	}

	int length(Object object);

	int concat(Object object, byte[] code, byte coder, int writeIndex);
}