package com.keimons.deepjson;

import com.keimons.deepjson.adapter.BytesConverter;

/**
 * 适配器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface IConverter<T> {

	IConverter<byte[]> BYTES_CONVERTER = new BytesConverter();

	T before(T dest, int length);

	void writeByte(T dest, int index, int value);
}