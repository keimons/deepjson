package com.keimons.deepjson;

import com.keimons.deepjson.adapter.BytesConverter;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * 生成器，用于将复合缓冲区生成至对应的数据结构
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Generator<T> {

	public static final Generator<byte[]> BYTES_GENERATOR = new Generator<byte[]>(new BytesConverter());

	protected IConverter<T> converter;

	public Generator(IConverter<T> converter) {
		this.converter = converter;
	}

	/**
	 * 生成策略
	 *
	 * @param dest        目标写入位置
	 * @param buffers     复合缓冲区
	 * @param length      总字节数
	 * @param bufferIndex 缓冲区的位置
	 * @param writeIndex  写入位置
	 * @return 写入完成后的数据
	 * @throws WriteFailedException 写入失败异常
	 */
	public T generate(T dest, char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		int size = Charsets.UTF_8.length(buffers, bufferIndex, writeIndex);
		T bytes = converter.before(dest, size);
		Charsets.UTF_8.encode(buffers, bufferIndex, writeIndex, converter, bytes);
		return bytes;
	}
}