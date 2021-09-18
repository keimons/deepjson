package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecException;

import java.lang.reflect.Type;

/**
 * 不兼容的类型异常
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IncompatibleTypeException extends CodecException {

	public IncompatibleTypeException() {

	}

	public IncompatibleTypeException(String message) {
		super(message);
	}

	/**
	 * 不能实例化异常
	 *
	 * @param type 类
	 */
	public IncompatibleTypeException(Type type) {
		super("cannot instantiation type: " + type);
	}

	public IncompatibleTypeException(Type type0, Type type1) {
		super("incompatible type: " + type0 + ", " + type1);
	}
}