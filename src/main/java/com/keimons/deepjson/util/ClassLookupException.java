package com.keimons.deepjson.util;

/**
 * 类型异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassLookupException extends RuntimeException {

	public ClassLookupException() {
		super();
	}

	public ClassLookupException(String message) {
		super(message);
	}

	public ClassLookupException(String message, Throwable cause) {
		super(message, cause);
	}
}