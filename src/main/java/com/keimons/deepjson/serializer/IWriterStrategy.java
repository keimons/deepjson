package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * 缓冲区写入策略
 * <p>
 * 写入策略包括：压缩字节写入策略、膨胀字节写入策略、char类型写入策略。
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public interface IWriterStrategy {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	/**
	 * 获取即将写入位置
	 *
	 * @return 即将写入位置
	 */
	int writeIndex();

	/**
	 * 写入char型mark
	 *
	 * @param mark 即将写入的值
	 */
	void writeMark(char mark);

	/**
	 * 写入boolean型值
	 *
	 * @param value 即将写入的值
	 */
	void writeValue(boolean value);

	/**
	 * 写入char型值
	 *
	 * @param value 即将写入的值
	 */
	void writeValue(char value);

	/**
	 * 写入int型值
	 *
	 * @param length 即将写入的长度
	 * @param value  即将写入的值
	 */
	void writeValue(int length, int value);

	/**
	 * 写入long型值
	 *
	 * @param length 即将写入的长度
	 * @param value  即将写入的值
	 */
	void writeValue(int length, long value);

	/**
	 * 写入字符串
	 *
	 * @param value 即将写入的值
	 */
	void writeValue(String value);

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

	void writeNull();
}