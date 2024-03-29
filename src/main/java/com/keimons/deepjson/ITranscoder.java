package com.keimons.deepjson;

/**
 * 复合缓冲区数组转码器
 * <p>
 * 当使用复合缓冲区时，对缓冲区中的内容进行转码。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface ITranscoder<T> {

	/**
	 * 计算编码后长度
	 *
	 * @param buffers     缓冲区
	 * @param length      总长度
	 * @param bufferIndex 最后的写入缓冲区
	 * @param writeIndex  最后的写入位置
	 * @return 长度
	 */
	int length(char[][] buffers, int length, int bufferIndex, int writeIndex);

	/**
	 * 对缓冲区进行编码后写入指定缓冲区
	 *
	 * @param buffers     要进行编码的缓冲区
	 * @param length      缓冲区长度
	 * @param bufferIndex 最后的写入缓冲区
	 * @param writerIndex 最后的写入位置
	 * @param converter   转化器
	 * @param dest        编码后的缓冲区
	 * @param offset      写入位置偏移
	 * @return 实际写入字节数
	 */
	T transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, IConverter<T> converter, T dest, int offset);
}