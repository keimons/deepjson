package com.keimons.deepjson.filler;

import com.keimons.deepjson.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * 填充数据
 */
public interface IFiller {

	/**
	 * 使用两个字节的分隔符
	 */
	byte[] UTF16_SPLIT = {
			(byte) (',' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) (',' >> FillerHelper.LO_BYTE_SHIFT),
	};

	/**
	 * 包装
	 */
	byte[] UTF16_QUOTATION = {
			(byte) ('"' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('"' >> FillerHelper.LO_BYTE_SHIFT),
	};

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	default byte coder(Object object) {
		return 1;
	}

	int length(Object object);

	int concat(Object object, byte[] code, byte coder, int writeIndex);
}