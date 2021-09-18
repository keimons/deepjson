package com.keimons.deepjson.util;

/**
 * 不支持的供应商
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UnsupportedException extends RuntimeException {

	public UnsupportedException() {
		super("unsupported jvm: " + System.getProperty("java.vm.name") + ", version: " + System.getProperty("java.vm.version"));
	}
}