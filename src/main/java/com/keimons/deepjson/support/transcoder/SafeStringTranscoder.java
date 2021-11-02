package com.keimons.deepjson.support.transcoder;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;
import com.keimons.deepjson.util.SimpleReference;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用最安全模式进行字符串连接
 * <p>
 * Ps:这也是最浪费内存的连接方式。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SafeStringTranscoder implements ITranscoder<String> {

	public static final SafeStringTranscoder instance = new SafeStringTranscoder();

	private final SimpleReference<char[]> CACHE = new SimpleReference<char[]>(null);

	private final Lock LOCK = new ReentrantLock();

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		return length;
	}

	@Override
	public String transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, IConverter<String> converter, String dest, int offset) {
		char[] buf;
		LOCK.lock();
		try {
			if ((buf = CACHE.get()) == null || buf.length < length) {
				if (length > 0x40000000) {
					buf = new char[length]; // 防止溢出
				} else {
					buf = new char[Integer.highestOneBit(length) << 1];
				}
				CACHE.set(buf);
			}
			int index = 0;
			for (int i = 0; i < bufferIndex; i++) {
				char[] buffer = buffers[i];
				System.arraycopy(buffer, 0, buf, index, buffer.length);
				index += buffer.length;
			}
			char[] buffer = buffers[bufferIndex];
			System.arraycopy(buffer, 0, buf, index, writerIndex);
			return new String(buf, 0, length);
		} finally {
			LOCK.unlock();
		}
	}
}