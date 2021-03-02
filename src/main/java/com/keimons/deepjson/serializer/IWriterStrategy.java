package com.keimons.deepjson.serializer;

import jdk.internal.vm.annotation.ForceInline;

/**
 * 写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public interface IWriterStrategy {

	int writeIndex();

	/**
	 * 写入boolean值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, boolean value);

	/**
	 * 写入char值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, char value);

	/**
	 * 写入int值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param length    值长度
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, int length, int value);

	/**
	 * 写入long值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param length    值长度
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, int length, long value);

	/**
	 * 写入string值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, String value);

	/**
	 * 写入object值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, Object value);

	/**
	 * 写入对象结尾
	 * <p>
	 * {@code '}'}表示对象结尾
	 */
	@ForceInline
	void writeEndObject();

	/**
	 * 写入数组结尾
	 * <p>
	 * {@code ']'}表示数组结尾
	 */
	@ForceInline
	void writeEndArray();
}