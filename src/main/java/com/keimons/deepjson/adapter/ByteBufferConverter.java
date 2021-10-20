package com.keimons.deepjson.adapter;

import com.keimons.deepjson.IConverter;

import java.nio.ByteBuffer;

/**
 * @author monkey1993
 * @version 1.0
 * @date 2021-10-20
 * @since 1.8
 **/
public class ByteBufferConverter implements IConverter<ByteBuffer> {

	@Override
	public ByteBuffer before(ByteBuffer dest, int length) {
		return dest;
	}

	@Override
	public void writeByte(ByteBuffer dest, int index, int value) {
		dest.put((byte) value);
	}
}