package com.keimons.deepjson.adapter;

import com.keimons.deepjson.IConverter;

/**
 * 空转化器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NoneConverter<T> implements IConverter<T> {

	@Override
	public T ensureWritable(T dest, int writable) {
		return dest;
	}

	@Override
	public void writeByte(T dest, int index, int value) {
		throw new UnsupportedOperationException();
	}
}