package com.keimons.deepjson.util;

/**
 * 非法调用者异常
 * <p>
 * 当发生非法调用时，抛出此异常。非法调用包括但不限于：非法版本，非法调用者等。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IllegalCallerException extends RuntimeException {

	/**
	 * 使用指定的java版本构造一个非法调用异常
	 *
	 * @param minVersion 指定的java版本
	 */
	public IllegalCallerException(int minVersion) {
		super("illegal java version, current: " + PlatformUtil.javaVersion() + ", min: " + minVersion);
	}

	/**
	 * 使用指定的调用者构造一个非法调用异常
	 *
	 * @param caller 调用者
	 */
	public IllegalCallerException(Class<?> caller) {
		super("illegal caller: " + caller);
	}
}