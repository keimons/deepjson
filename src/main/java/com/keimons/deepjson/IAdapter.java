package com.keimons.deepjson;

/**
 * 适配器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface IAdapter {

	void before(int length);

	void writeByte(int value);
}