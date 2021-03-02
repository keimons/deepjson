package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

public class CharBuf implements IBuffer<char[]> {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final long options;

	private IWriterStrategy writer;

	private char[] buf;

	private int writeIndex;

	private CharBuf(long options, int capacity) {
		this.buf = new char[capacity];
		this.options = options;
	}
}