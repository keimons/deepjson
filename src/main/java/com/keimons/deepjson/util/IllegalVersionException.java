package com.keimons.deepjson.util;

/**
 * 非法版本异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class IllegalVersionException extends RuntimeException {

	/**
	 * 使用指定的java版本构造一个异常
	 *
	 * @param minVersion 指定的java版本
	 */
	public IllegalVersionException(int minVersion) {
		super("illegal java version, current: " + PlatformUtil.javaVersion() + ", min: " + minVersion);
	}
}