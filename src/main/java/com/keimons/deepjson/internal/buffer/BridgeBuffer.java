package com.keimons.deepjson.internal.buffer;

import com.keimons.deepjson.Buffer;
import com.keimons.deepjson.Generator;
import com.keimons.deepjson.util.WriteFailedException;

import java.io.IOException;
import java.io.Writer;

/**
 * 桥接缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BridgeBuffer implements Buffer {

	Writer writer;

	public BridgeBuffer(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void write(char value) throws IOException {
		writer.append(value);
	}

	@Override
	public void safeWrite(char value) throws IOException {
		writer.append(value);
	}

	@Override
	public boolean ensureWritable(int writable) {
		return true;
	}

	@Override
	public <T> T writeTo(Generator<T> generator, T dest, int offset) throws WriteFailedException {
		// do not call by deepjson.
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws IOException {
		// do not call by deepjson.
		throw new UnsupportedOperationException();
	}
}