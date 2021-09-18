package com.keimons.deepjson.util;

import sun.misc.Unsafe;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
 * 缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SimpleReference<T> extends SoftReference<T> {

	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

	// 引用偏移量
	private static final long OFFSET;

	static {
		long offset = -1L;
		try {
			offset = UNSAFE.objectFieldOffset(Reference.class.getDeclaredField("referent"));
		} catch (NoSuchFieldException e) {
			// ignore
			// TODO SafeModule
		}
		OFFSET = offset;
	}

	/**
	 * 构造方法
	 *
	 * @param referent 引用
	 */
	public SimpleReference(T referent) {
		super(referent);
	}

	/**
	 * 设置引用
	 *
	 * @param referent 引用
	 */
	public void set(T referent) {
		UNSAFE.putObject(this, OFFSET, referent);
	}
}