package com.keimons.deepjson.charset;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;

/**
 * 字符数组编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharsTranscoder implements ITranscoder {

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		return length;
	}

	@Override
	public <T> int encode(char[][] buffers, int bufferIndex, int writeIndex, IConverter<T> converter, T dest, int offset) {

		return 0;
	}
}