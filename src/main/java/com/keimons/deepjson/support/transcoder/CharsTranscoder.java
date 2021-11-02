package com.keimons.deepjson.support.transcoder;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;

/**
 * {@code char[]}写入工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharsTranscoder implements ITranscoder<char[]> {

	public static final CharsTranscoder instance = new CharsTranscoder();

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		return length;
	}

	@Override
	public char[] transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, IConverter<char[]> converter, char[] dest, int offset) {
		char[] chars = new char[length];
		int index = 0;
		for (int i = 0; i < bufferIndex; i++) {
			char[] buffer = buffers[i];
			System.arraycopy(buffer, 0, chars, index, buffer.length);
			index += buffer.length;
		}
		System.arraycopy(buffers[bufferIndex], 0, chars, index, writerIndex);
		return chars;
	}
}