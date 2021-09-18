package com.keimons.deepjson;

/**
 * {@code float}写入策略
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public interface IDecimalStrategy {

	/**
	 * 将一个单精度浮点数写入缓冲区
	 *
	 * @param value      浮点数
	 * @param buf        缓冲区
	 * @param writeIndex 起始位置
	 * @return 写入字符数
	 */
	int write(float value, char[] buf, int writeIndex);

	/**
	 * 将一个双精度浮点数写入缓冲区
	 *
	 * @param value      浮点数
	 * @param buf        缓冲区
	 * @param writeIndex 起始位置
	 * @return 写入字符数
	 */
	int write(double value, char[] buf, int writeIndex);
}