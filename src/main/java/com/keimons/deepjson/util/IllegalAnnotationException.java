package com.keimons.deepjson.util;

import com.keimons.deepjson.CodecException;

/**
 * 非法注解异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IllegalAnnotationException extends CodecException {

	public IllegalAnnotationException() {

	}

	public IllegalAnnotationException(String message) {
		super(message);
	}

	public IllegalAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}
}