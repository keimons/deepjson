package com.keimons.deepjson.support.writer;

import com.keimons.deepjson.AbstractWriter;
import com.keimons.deepjson.Charsets;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * 返回{@code byte[]}
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UTF8BytesWriter extends AbstractWriter<byte[]> {

	public static final UTF8BytesWriter instance = new UTF8BytesWriter();

	@Override
	public byte[] write(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		int size = Charsets.UTF_8.length(buffers, bufferIndex, writeIndex);
		byte[] bytes = new byte[size];
		Charsets.UTF_8.encode(buffers, bufferIndex, writeIndex, bytes);
		return bytes;
	}
}