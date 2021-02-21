package com.keimons.deepjson;

/**
 * 序列化选项
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public enum SerializerOptions {

	/**
	 * (序列化/反序列化)忽略空字段
	 */
	IgnoreNonField,

	/**
	 * 包含类名
	 */
	IncludeClassName,

	/**
	 * (反序列化)忽略final字段
	 */
	IncludeFinalField;

	long optional;

	SerializerOptions() {
		this.optional = 1L << ordinal();
	}

	public static long getOptions(SerializerOptions... options) {
		long optional = 0;
		for (SerializerOptions option : options) {
			optional |= option.optional;
		}
		return optional;
	}

	public boolean isOptions(long options) {
		return (optional & options) == 1;
	}
}