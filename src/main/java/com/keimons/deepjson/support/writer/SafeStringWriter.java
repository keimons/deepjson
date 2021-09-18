package com.keimons.deepjson.support.writer;

import com.keimons.deepjson.AbstractWriter;
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
public class SafeStringWriter extends AbstractWriter<String> {

	public static final SafeStringWriter instance = new SafeStringWriter();

	private final SimpleReference<char[]> CACHE = new SimpleReference<char[]>(null);

	private final Lock LOCK = new ReentrantLock();

	@Override
	public String write(char[][] buffers, int length, int bufferIndex, int writeIndex) {
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
			System.arraycopy(buffer, 0, buf, index, writeIndex);
			return new String(buf, 0, length);
		} finally {
			LOCK.unlock();
		}
	}
}