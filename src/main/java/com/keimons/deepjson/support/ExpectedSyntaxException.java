package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecException;

import java.util.Arrays;

/**
 * 预期{@link SyntaxToken}异常
 * <p>
 * 标记语法错误位置，指出当前语法以及期望语法。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ExpectedSyntaxException extends CodecException {

	public ExpectedSyntaxException() {

	}

	/**
	 * 构造一个语法异常
	 *
	 * @param buf        缓冲区
	 * @param startIndex 标记位置
	 * @param current    当前语法
	 * @param targets    目标语法
	 */
	public ExpectedSyntaxException(char[] buf, int startIndex, SyntaxToken current, SyntaxToken... targets) {
		super("syntax error at " + startIndex +
				": " + buildMessage(buf, startIndex) +
				", token: " + current +
				", expected: " + Arrays.toString(targets)
		);
	}
}