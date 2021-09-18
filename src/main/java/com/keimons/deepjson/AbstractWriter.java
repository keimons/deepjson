package com.keimons.deepjson;

import com.keimons.deepjson.util.WriteFailedException;

/**
 * 写入工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractWriter<T> {

	/**
	 * 写入策略
	 *
	 * @param buffers     复合缓冲区
	 * @param length      总字节数
	 * @param bufferIndex 缓冲区的位置
	 * @param writeIndex  写入位置
	 * @return 写入完成后的数据
	 * @throws WriteFailedException 写入失败异常
	 */
	public abstract T write(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException;
}