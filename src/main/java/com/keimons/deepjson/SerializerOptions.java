package com.keimons.deepjson;

/**
 * 序列化选项
 * <p>
 * {@link sun.misc.Unsafe Unsafe)功能过于强大。既要充分利用，也需要一些限制。
 * <p>
 * 命名规则：
 * <ul>
 *     <li>Ignore*  忽略</li>
 *     <li>Include* 包含</li>
 *     <li>Force*   无视规则</li>
 * </ul>
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public enum SerializerOptions {

	/**
	 * 忽略空字段
	 * <p>
	 * decode: true, encode: false
	 */
	IgnoreNonField,

	/**
	 * 包含类名
	 * <p>
	 * decode: false, encode: true
	 */
	IncludeClassName,

	/**
	 * 包含final字段
	 * <p>
	 * decode: false, encode: true
	 */
	ForceFinalField,

	/**
	 * 包含transient字段
	 * <p>
	 * decode: true, encode: true
	 */
	ForceTransientField,

	/**
	 * 包含同名字段
	 * <p>
	 * decode: true, encode: true
	 */
	ForceSameNameField;

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