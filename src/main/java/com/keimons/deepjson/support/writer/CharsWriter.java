package com.keimons.deepjson.support.writer;

import com.keimons.deepjson.AbstractWriter;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * {@code char[]}写入工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharsWriter extends AbstractWriter<char[]> {

	public static final CharsWriter instance = new CharsWriter();

	@Override
	public char[] write(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		char[] chars = new char[length];
		int index = 0;
		for (int i = 0; i < bufferIndex; i++) {
			char[] buffer = buffers[i];
			System.arraycopy(buffer, 0, chars, index, buffer.length);
			index += buffer.length;
		}
		System.arraycopy(buffers[bufferIndex], 0, chars, index, writeIndex);
		return chars;
	}
}