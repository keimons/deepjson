package com.keimons.deepjson.util;

/**
 * 类型查找失败异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypeNotFoundException extends RuntimeException {

	public TypeNotFoundException() {
		super();
	}

	public TypeNotFoundException(String message) {
		super(message);
	}

	public TypeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}