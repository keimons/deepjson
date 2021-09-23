package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecException;

/**
 * 实例化失败异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class InstantiationFailedException extends CodecException {

	public InstantiationFailedException() {
		super();
	}

	public InstantiationFailedException(String message) {
		super(message);
	}

	public InstantiationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}