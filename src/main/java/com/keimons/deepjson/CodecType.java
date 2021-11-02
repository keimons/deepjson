package com.keimons.deepjson;

/**
 * 编解码类型
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum CodecType {

	ENCODE(0x1), DECODE(0x2), CODEC(0x3);

	private final int option;

	CodecType(int option) {
		this.option = option;
	}

	public static boolean isEncode(CodecType type) {
		return (type.option & 0x1) != 0;
	}

	public static boolean isDecode(CodecType type) {
		return (type.option & 0x2) != 0;
	}
}