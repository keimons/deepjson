package com.keimons.deepjson;

import com.keimons.deepjson.adapter.BytesConverter;
import com.keimons.deepjson.adapter.NoneConverter;
import com.keimons.deepjson.support.transcoder.CharsTranscoder;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * 生成器，用于将复合缓冲区生成至对应的数据结构
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Generator<T> {

	public static final Generator<byte[]> GENERATOR_BYTES_UTF8 = new Generator<byte[]>(Charsets.UTF_8, new BytesConverter());

	public static final Generator<char[]> CHAR_ARRAY = new Generator<char[]>(CharsTranscoder.instance, new NoneConverter<char[]>());

	/**
	 * 转化器
	 */
	protected IConverter<T> converter;

	/**
	 * 转码器
	 */
	protected ITranscoder<T> transcoder;

	/**
	 * 构造方法
	 *
	 * @param transcoder 转码器
	 * @param converter  转化器
	 */
	public Generator(ITranscoder<T> transcoder, IConverter<T> converter) {
		this.transcoder = transcoder;
		this.converter = converter;
	}

	/**
	 * 生成策略
	 *
	 * @param dest        写入目标
	 * @param offset      偏移位置
	 * @param buffers     复合缓冲区
	 * @param length      总字节数
	 * @param bufferIndex 缓冲区的位置
	 * @param writeIndex  写入位置
	 * @return 写入完成后的数据
	 * @throws WriteFailedException 写入失败异常
	 */
	public T generate(T dest, int offset, char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		int size = transcoder.length(buffers, length, bufferIndex, writeIndex);
		T target = converter.ensureWritable(dest, size);
		return transcoder.transcoder(buffers, length, bufferIndex, writeIndex, converter, target, offset);
	}
}