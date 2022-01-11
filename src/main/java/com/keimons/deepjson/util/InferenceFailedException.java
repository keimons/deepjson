package com.keimons.deepjson.util;

import com.keimons.deepjson.CodecException;

/**
 * 推导失败异常
 * <p>
 * 常用于类型、变量名的推导失败时。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class InferenceFailedException extends CodecException {

	public InferenceFailedException() {

	}

	public InferenceFailedException(String message) {
		super(message);
	}

	public InferenceFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}