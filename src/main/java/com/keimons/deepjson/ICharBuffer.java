package com.keimons.deepjson;

/**
 * 缓冲区缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface ICharBuffer {

	/**
	 * 设置缓冲区缓存
	 */
	void set(char[] buf);

	/**
	 * 获取缓冲区缓存
	 *
	 * @return 缓冲区
	 */
	char[] get();

	/**
	 * 是否缓存中
	 *
	 * @return 是否缓存
	 */
	boolean isCached();

	/**
	 * 标记一个缓冲区正在使用
	 */
	void mark();

	/**
	 * 释放缓冲区
	 */
	void release();
}