package com.keimons.deepjson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 编解码选项
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum CodecOptions {

	/**
	 * 开启Debug模式
	 * <p>
	 * Debug模式会在控制台输出编码后的字符串。
	 * <p>
	 * decode: true, encode: true
	 */
	Debug,

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
	WriteFinalField,

	/**
	 * 包含transient字段
	 * <p>
	 * decode: true, encode: true
	 */
	WriteTransientField,

	/**
	 * 包含同名字段
	 * <p>
	 * decode: true, encode: true
	 */
	WriteSameField,

	/**
	 * 包含类型（支持自定义白名单）
	 * <p>
	 * 将类型信息写入json中。此选项主要用于参数类型声明为接口或抽象类时，无法转换回原对象的问题。
	 * 例如：
	 * {@link List}的实现可能是{@link ArrayList}或{@link LinkedList}。
	 * {@link Map}的实现可能是{@link HashMap}或{@link ConcurrentHashMap}。
	 * <p>
	 * 警告：如果转换失败或者与目标接口不符时，此选项将会被忽略。
	 * <p>
	 * decode: true, encode: true
	 *
	 * @see CodecConfig 编解码选项
	 */
	WriteClassName,

	/**
	 * 强制使用unicode编码。
	 * <p>
	 * 正常情况下仅仅会将一些不可见或需要转码的字符使用unicode编码，一旦启用了
	 * 这个规则，则所有{@code char}和{@code String}都将使用unicode编码。
	 * <p>
	 * decode: false, encode: true
	 */
	WriteUsingUnicode,

	/**
	 * {@link Map}中的{@code key}保留基础类型。
	 * <p>
	 * decode: false, encode: true
	 * <p>
	 * eg:
	 * {"5.0":5.0,"a":"b","1":1,"2":2,"3":3,"4":4,"false":true,"6.0":6.0}
	 * ----&lt;
	 * {5.0:5.0,"a":"b",1:1,2:2,3:3,4:4,false:true,6.0:6.0}
	 */
	PrimitiveKey,
	;

	long optional;

	CodecOptions() {
		this.optional = 1L << ordinal();
	}

	public static long getOptions(CodecOptions... options) {
		long optional = 0;
		for (CodecOptions option : options) {
			optional |= option.optional;
		}
		return optional;
	}

	public boolean isOptions(long options) {
		return (optional & options) != 0;
	}

	public boolean noOptions(long options) {
		return (optional & options) == 0;
	}
}