package com.keimons.deepjson;

import com.keimons.deepjson.adapter.BytesConverter;

/**
 * 写入适配器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface IConverter<T> {

	IConverter<byte[]> BYTES_CONVERTER = new BytesConverter();

	/**
	 * 确保{@code dest}中可以写入指定的长度
	 *
	 * @param dest     写入目标
	 * @param writable 即将写入长度
	 * @return 要写入的目标
	 * @see Generator 生成时，确保可以写入
	 */
	T ensureWritable(T dest, int writable);

	/**
	 * 写入{@code byte}值
	 *
	 * @param dest  写入目标
	 * @param index 写入位置
	 * @param value 写入值
	 */
	void writeByte(T dest, int index, int value);
}