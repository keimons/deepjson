package com.keimons.deepjson.annotation;

/**
 * 序列化选项
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public @interface CodecField {

	/**
	 * 返回预设方案
	 *
	 * @return 预设方案
	 */
	Preset format() default @Preset;
}