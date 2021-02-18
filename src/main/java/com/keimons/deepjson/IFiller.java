package com.keimons.deepjson;

import sun.misc.Unsafe;

public interface IFiller {

	byte[] UTF16_SPLIT = {
			(byte) (',' >> DeepHelper.HI_BYTE_SHIFT),
			(byte) (',' >> DeepHelper.LO_BYTE_SHIFT),
	};

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	default byte coder(Object object) {
		return 1;
	}

	int length(Object object);

	int concat(Object object, byte[] code, byte coder, int writeIndex);
}