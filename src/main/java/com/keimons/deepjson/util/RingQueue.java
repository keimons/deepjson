package com.keimons.deepjson.util;

import sun.misc.Unsafe;

/**
 * 线程安全的环形队列
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class RingQueue<T> {

	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
	transient final T[] nodes;
	private final int mark;
	private final int size;
	transient volatile int writeIndex;
	transient volatile int readIndex;

	/**
	 * 构造方法
	 *
	 * @param capacity 环形队列长度
	 */
	public RingQueue(int capacity) {
		if (Integer.bitCount(capacity) != 1) {
			throw new IllegalArgumentException("capacity mast be 2 pow.");
		}
		this.size = capacity;
		this.mark = capacity - 1;
		this.nodes = ArrayUtil.newInstance(Object.class, capacity);
	}

	public boolean enqueue(T node) {
		for (; ; ) {
			if (writeIndex - readIndex >= size) {
				return false;
			}
			int offset = UnsafeUtil.ARRAY_OBJECT_BASE_OFFSET + UnsafeUtil.ARRAY_OBJECT_INDEX_SCALE * (writeIndex & mark);
			if (UNSAFE.compareAndSwapObject(nodes, offset, null, node)) {
				writeIndex++;
				return true;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T dequeue() {
		for (; ; ) {
			if (writeIndex - readIndex <= 0) {
				return null;
			}
			T cache = nodes[readIndex];
			int offset = UnsafeUtil.ARRAY_OBJECT_BASE_OFFSET + UnsafeUtil.ARRAY_OBJECT_INDEX_SCALE * (readIndex & mark);
			if (UNSAFE.compareAndSwapObject(nodes, offset, cache, null)) {
				readIndex++;
				return cache;
			}
		}
	}
}