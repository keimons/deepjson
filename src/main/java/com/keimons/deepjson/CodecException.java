package com.keimons.deepjson;

/**
 * 编解码异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class CodecException extends RuntimeException {

	public CodecException() {

	}

	/**
	 * 构造一个解码异常
	 *
	 * @param message 异常信息
	 */
	public CodecException(String message) {
		super(message);
	}

	/**
	 * 构造异常信息
	 *
	 * @param buf        缓冲区
	 * @param startIndex 标记位置
	 * @return 异常信息
	 */
	public static String buildMessage(final char[] buf, final int startIndex) {
		char[] chars = new char[22];
		int index = 0;
		// 复制前10个字符
		for (int i = Math.max(0, startIndex - 10); i < startIndex; i++) {
			chars[index++] = buf[i];
		}
		if (index != 0) {
			chars[index++] = ' ';
		}
		chars[index++] = '→';
		// 复制后10个字符
		for (int i = startIndex, limit = Math.min(buf.length, startIndex + 10); i < limit; i++) {
			chars[index++] = buf[i];
		}
		return new String(chars, 0, index);
	}
}