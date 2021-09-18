package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecException;

/**
 * 未知语法异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UnknownSyntaxException extends CodecException {


	public UnknownSyntaxException() {

	}

	/**
	 * 构造一个未知语法异常
	 *
	 * @param message 异常信息
	 */
	public UnknownSyntaxException(String message) {
		super(message);
	}

	/**
	 * 构造一个未知语法异常
	 *
	 * @param buf        缓冲区
	 * @param startIndex 标记位置
	 */
	public UnknownSyntaxException(final char[] buf, final int startIndex) {
		super("unknown syntax at " + startIndex + ": " + buildMessage(buf, startIndex));
	}

	/**
	 * 构造一个未知语法异常
	 * <p>
	 * 会在异常信息后拼接{@code " at number: \"context →error context\""}信息。
	 *
	 * @param message    异常信息
	 * @param buf        缓冲区
	 * @param startIndex 标记位置
	 */
	public UnknownSyntaxException(String message, char[] buf, int startIndex) {
		super(message + " " + startIndex + ": " + buildMessage(buf, startIndex));
	}
}