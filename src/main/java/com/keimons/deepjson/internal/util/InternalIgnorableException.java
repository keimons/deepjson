package com.keimons.deepjson.internal.util;

/**
 * 内部可忽略的必检异常
 * <p>
 * 通常情况下，对于可忽略的必检异常会有相应的兼容方案。这个异常不会对外暴露。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class InternalIgnorableException extends Exception {

	public InternalIgnorableException() {

	}

	public InternalIgnorableException(String message) {
		super(message);
	}

	public InternalIgnorableException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalIgnorableException(Throwable cause) {
		super(cause);
	}

	public InternalIgnorableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}