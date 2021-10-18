package com.keimons.deepjson.support.generator;

import com.keimons.deepjson.AbstractGenerator;
import com.keimons.deepjson.Charsets;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * 返回{@code byte[]}
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UTF8BytesGenerator extends AbstractGenerator<byte[]> {

	public static final UTF8BytesGenerator instance = new UTF8BytesGenerator();

	@Override
	public byte[] generate(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		int size = Charsets.UTF_8.length(buffers, bufferIndex, writeIndex);
		byte[] bytes = new byte[size];
		Charsets.UTF_8.encode(buffers, bufferIndex, writeIndex, bytes);
		return bytes;
	}
}