package com.keimons.deepjson.util;

/**
 * 初始化编解码器失败异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class InitializeCodecFailedException extends RuntimeException {

	public InitializeCodecFailedException() {

	}

	public InitializeCodecFailedException(String message) {
		super(message);
	}

	public InitializeCodecFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializeCodecFailedException(Throwable cause) {
		super(cause);
	}
}