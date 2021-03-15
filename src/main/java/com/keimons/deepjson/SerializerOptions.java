package com.keimons.deepjson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
 * @author monkey
 * @version 1.0
 * @since 1.7
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
	ForceSameField,

	/**
	 * 包含类型注释（支持自定义白名单）
	 * <p>
	 * 根据Javascript大神Douglas Crockford的说法，很多人利用注释来制定解析规则，这破坏了
	 * 互操作性（Interoperability）。因此大神将其剔除。
	 * <br>
	 * 根据<a href="https://json5.org/">Json5的规范</a>，允许使用{@code "&#47;&#47;"}
	 * 和{@code "&#47;&#42;&#42;&#47;"}作为注释。DeepJson允许使用注释描述解析类型。例如：
	 * {@code "[&#47;&#42;@type:java.util.List&#42;&#47;]"}表示这是{@link ArrayList}
	 * 类型的对象。
	 * <br>
	 * 此选项主要用于参数类型声明为接口或抽象类时，无法转换回原对象的问题。
	 * 例如：
	 * {@link List}的实现可能是{@link ArrayList}或{@link LinkedList}。
	 * {@link Map}的实现可能是{@link HashMap}或{@link ConcurrentHashMap}。
	 * <p>
	 * 警告：如果转换失败或者与目标接口不符时，此选项将会被忽略。
	 * <br>
	 * 警告：启用此功能会导致部分解析工具无法解析此json。
	 * <p>
	 * decode: true, encode: true
	 */
	@Deprecated
	ForceClassName;

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
		return (optional & options) != 0;
	}
}