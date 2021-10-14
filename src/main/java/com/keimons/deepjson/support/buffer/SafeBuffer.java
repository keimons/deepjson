package com.keimons.deepjson.support.buffer;

import com.keimons.deepjson.WriterBuffer;

import java.util.Arrays;

/**
 * 安全模式缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SafeBuffer extends WriterBuffer {

	public SafeBuffer() {
		super(2);
	}

	@Override
	protected void writeStringUnicode(String value) {
		buf[writeIndex++] = '"';
		for (char c : value.toCharArray()) {
			writeUnicode(c);
		}
		buf[writeIndex++] = '"';
	}

	@Override
	protected void writeStringNormal(String value) {
		buf[writeIndex++] = '"';
		for (char c : value.toCharArray()) {
			writeNormal(c);
		}
		buf[writeIndex++] = '"';
	}

	@Override
	protected boolean expandCapacity(int minCapacity) {
		int newCapacity = capacity;
		while (newCapacity < minCapacity) {
			newCapacity = capacity << 1;
			buf = Arrays.copyOf(buf, newCapacity);
			capacity = newCapacity;
			buffers[0] = buf;
		}
		if (capacity <= 256 * 1024) {
			buffers[1] = buffers[0];
		}
		return false;
	}

	@Override
	public void close() {
		char[] cache = buffers[1];
		if (cache != null) {
			buffers[0] = cache;
			buffers[1] = null;
		}
	}
}