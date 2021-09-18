package com.keimons.deepjson.pool;

import com.keimons.deepjson.Config;
import com.keimons.deepjson.ICharBuffer;
import com.keimons.deepjson.util.RingQueue;

/**
 * 缓存解决方案
 * <p>
 * 4k -> 16k -> 64k -> 256k
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class PooledBufferFactory {

	public static final int THREAD_LOCAL_BUFFER_SIZE = 1;

	public static final int BUFFER_CACHE_SIZE = 32;

	private static final RingQueue<ICharBuffer> queue = new RingQueue<ICharBuffer>(BUFFER_CACHE_SIZE);

	public static ICharBuffer borrowBuffer(int bufferIndex) {
		if (bufferIndex < THREAD_LOCAL_BUFFER_SIZE) {
			return new DefaultCharBuffer(4 * 1024);
		}
		ICharBuffer cache = queue.dequeue();
		if (cache == null) {
			cache = new SoftCharBuffer(1 << Config.HIGHEST);
		}
		cache.mark();
		return cache;
	}

	public static void returnBuffer(ICharBuffer buffer) {
		if (buffer != null && buffer.isCached()) {
			buffer.release();
			queue.enqueue(buffer);
		}
	}
}