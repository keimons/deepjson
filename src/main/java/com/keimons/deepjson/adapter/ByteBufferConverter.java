package com.keimons.deepjson.adapter;

import com.keimons.deepjson.IConverter;

import java.nio.ByteBuffer;

/**
 * {@link ByteBuffer}转化器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteBufferConverter implements IConverter<ByteBuffer> {

	@Override
	public ByteBuffer ensureWritable(ByteBuffer dest, int writable) {
		if (dest == null) {
			return ByteBuffer.allocate(writable);
		}
		return dest;
	}

	@Override
	public void writeByte(ByteBuffer dest, int index, int value) {
		dest.put((byte) value);
	}
}