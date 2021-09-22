package com.keimons.deepjson.annotation;

/**
 * 预设方案
 * <p>
 * 直接调用已经预设的方案。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public @interface Preset {

	String formatter() default "default";
}