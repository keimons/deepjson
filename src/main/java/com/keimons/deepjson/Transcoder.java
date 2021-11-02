package com.keimons.deepjson;

/**
 * 复合缓冲区转码器
 * <p>
 * 当使用复合缓冲区时，对缓冲区中的内容进行转码。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class Transcoder<T> {

	public abstract T transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, T dest);
}