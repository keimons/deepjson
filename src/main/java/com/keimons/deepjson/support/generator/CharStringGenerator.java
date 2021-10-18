package com.keimons.deepjson.support.generator;

import com.keimons.deepjson.AbstractGenerator;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * {@code char[]}字符串连接工具，适用于jdk1.8及以下。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharStringGenerator extends AbstractGenerator<String> {

	@Override
	public String generate(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		try {
			int index = 0;
			char[] buf = new char[length];
			for (int i = 0; i < bufferIndex; i++) {
				char[] buffer = buffers[i];
				System.arraycopy(buffer, 0, buf, index, buffer.length);
				index += buffer.length;
			}
			char[] buffer = buffers[bufferIndex];
			System.arraycopy(buffer, 0, buf, index, writeIndex);

			return new String(buf, 0, length);
		} catch (Throwable cause) {
			throw new WriteFailedException(cause);
		}
	}
}