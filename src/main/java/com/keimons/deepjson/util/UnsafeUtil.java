package com.keimons.deepjson.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Unsafe工具操作类
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class UnsafeUtil {

	/**
	 * 获取Unsafe实例
	 */
	public static final Unsafe UNSAFE;

	static {
		try {
			final PrivilegedExceptionAction<Unsafe> action = () -> {
				Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
				theUnsafe.setAccessible(true);
				return (Unsafe) theUnsafe.get(null);
			};
			UNSAFE = AccessController.doPrivileged(action);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load unsafe", e);
		}
	}

	/**
	 * 获取Unsafe类
	 *
	 * @return unsafe
	 */
	public static Unsafe getUnsafe() {
		return UNSAFE;
	}
}