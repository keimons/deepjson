package com.keimons.deepjson.support.transcoder;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * {@code char[]}字符串连接工具，适用于jdk1.8及以下。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharStringTranscoder implements ITranscoder<String> {

	public static final CharStringTranscoder instance = new CharStringTranscoder();

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		return length;
	}

	@Override
	public String transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, IConverter<String> converter, String dest, int offset) {
		try {
			int index = 0;
			char[] buf = new char[length];
			for (int i = 0; i < bufferIndex; i++) {
				char[] buffer = buffers[i];
				System.arraycopy(buffer, 0, buf, index, buffer.length);
				index += buffer.length;
			}
			char[] buffer = buffers[bufferIndex];
			System.arraycopy(buffer, 0, buf, index, writerIndex);

			return new String(buf, 0, length);
		} catch (Throwable cause) {
			throw new WriteFailedException(cause);
		}
	}
}