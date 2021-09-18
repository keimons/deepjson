package com.keimons.deepjson.support.codec;

/**
 * 编解码异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IllegalCodecException extends RuntimeException {

	public IllegalCodecException() {
		super();
	}

	public IllegalCodecException(String message) {
		super(message);
	}
}