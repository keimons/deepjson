package com.keimons.deepjson.util;

import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

/**
 * 线程安全的环形队列
 * <p>
 * 环形队列的写入和读取都是简单的set方法
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class RingQueue<T> extends RPadding<T> {

	transient volatile int writeIndex;
	transient volatile int readIndex;

	/**
	 * 构造方法
	 *
	 * @param capacity 环形队列长度
	 */
	public RingQueue(int capacity) {
		super(capacity);
	}

	/**
	 * 存入节点到环形缓冲区尾
	 * <p>
	 * 节点存入应该仅仅是等价于一个set，不需要很复杂的等待逻辑，原地自旋即可。
	 *
	 * @param node 节点
	 * @return 是否存入
	 */
	public boolean enqueue(T node) {
		for (; ; ) {
			final int writeIndex = this.writeIndex;
			if (writeIndex - readIndex >= size) {
				return false;
			}
			if (cas(writeIndex, null, node)) {
				this.writeIndex = writeIndex + 1;
				return true;
			}
		}
	}

	/**
	 * 取出当前环形缓冲区的首节点
	 *
	 * @return 首节点
	 */
	public @Nullable T dequeue() {
		for (; ; ) {
			final int readIndex = this.readIndex;
			if (writeIndex - readIndex <= 0) {
				return null;
			}
			T cache = get(readIndex);
			if (cas(readIndex, cache, null)) {
				this.readIndex = readIndex + 1;
				return cache;
			}
		}
	}
}

abstract class LPadding {

	protected long L0, L1, L2, L3, L4, L5, L6, L7;
}

abstract class VPadding<T> extends LPadding {

	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	/**
	 * 左右两侧预留 16 * 4，64字节
	 */
	private static final int BUFFER_PAD = 16;

	private static final int OFFSET = UnsafeUtil.ARRAY_OBJECT_BASE_OFFSET + UnsafeUtil.ARRAY_OBJECT_INDEX_SCALE * BUFFER_PAD;

	private final int mark;

	private final T[] nodes;

	protected final int size;

	public VPadding(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("capacity must be more than 0");
		}
		if (Integer.bitCount(capacity) != 1) {
			throw new IllegalArgumentException("capacity must be a power of 2");
		}
		this.nodes = ArrayUtil.newInstance(Object.class, capacity + BUFFER_PAD * 2);
		this.size = capacity;
		this.mark = capacity - 1;
	}

	protected T get(int index) {
		return nodes[BUFFER_PAD + (index & mark)];
	}

	protected boolean cas(long index, Object expected, Object newValue) {
		return UNSAFE.compareAndSwapObject(nodes, OFFSET + (index & mark) * UnsafeUtil.ARRAY_OBJECT_INDEX_SCALE, expected, newValue);
	}
}

abstract class RPadding<T> extends VPadding<T> {

	protected long R0, R1, R2, R3, R4, R5, R6, R7;

	public RPadding(int capacity) {
		super(capacity);
		must0();
		must1();
	}

	protected void must0() {
		L0 = 1;
		L1 = 1;
		L2 = 1;
		L3 = 1;
		L4 = 1;
		L5 = 1;
		L6 = 1;
		L7 = 1;
		R0 = 1;
		R1 = 1;
		R2 = 1;
		R3 = 1;
		R4 = 1;
		R5 = 1;
		R6 = 1;
		R7 = 1;
	}

	protected long must1() {
		return L0 + L1 + L2 + L3 + L4 + L5 + L6 + L7 + R0 + R1 + R2 + R3 + R4 + R5 + R6 + R7;
	}
}