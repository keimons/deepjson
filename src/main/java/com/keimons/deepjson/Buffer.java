package com.keimons.deepjson;

import com.keimons.deepjson.util.WriteFailedException;

import java.io.Closeable;
import java.io.IOException;

/**
 * 缓冲区实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface Buffer extends Closeable {

	/**
	 * 写入一个字符
	 *
	 * @param value 字符
	 * @throws IOException 写入异常
	 */
	void write(char value) throws IOException;

	/**
	 * 安全模式写入一个字符
	 *
	 * @param value 字符
	 * @throws IOException 写入异常
	 */
	void safeWrite(char value) throws IOException;

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writable 即将写入的字节数
	 * @return 是否横跨缓冲区。{@code true}横跨缓冲区写入，{@code false}当前缓冲区写入。
	 */
	boolean ensureWritable(int writable);

	<T> T writeTo(Generator<T> generator, T dest, int offset) throws WriteFailedException;
}