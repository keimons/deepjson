package com.keimons.deepjson.util;

/**
 * 写入失败异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class WriteFailedException extends RuntimeException {

	public WriteFailedException() {

	}

	public WriteFailedException(Throwable cause) {
		super(cause);
	}
}