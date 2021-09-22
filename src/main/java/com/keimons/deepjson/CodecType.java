package com.keimons.deepjson;

/**
 * 编解码类型
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum CodecType {

	ENCODE(1), DECODE(2), CODEC(3);

	private final int option;

	CodecType(int option) {
		this.option = option;
	}

	public static boolean isEncode(CodecType type) {
		return (type.option & 1) != 0;
	}

	public static boolean isDecode(CodecType type) {
		return (type.option & 2) != 0;
	}
}