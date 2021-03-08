package com.keimons.deepjson.buffer;

import com.keimons.deepjson.compiler.IFieldName;
import com.keimons.deepjson.util.UnsafeUtil;
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

	int offset = unsafe.arrayBaseOffset(byte[].class);

	/**
	 * 设置缓冲区
	 *
	 * @param buf 新缓冲区
	 */
	default void setByteBuf(byte[] buf) {
		throw new RuntimeException();
	}

	/**
	 * 设置缓冲区
	 *
	 * @param buf 新缓冲区
	 */
	default void setCharBuf(char[] buf) {
		throw new RuntimeException();
	}

	/**
	 * 获取缓冲区
	 *
	 * @return 字节缓冲区
	 */
	default byte[] getByteBuf() {
		throw new RuntimeException();
	}

	/**
	 * 获取缓冲区
	 *
	 * @return 字符缓冲区
	 */
	default char[] getCharBuf() {
		throw new RuntimeException();
	}

	/**
	 * 获取即将写入位置
	 *
	 * @return 即将写入位置
	 */
	int writeIndex();

	/**
	 * 检测是否可写入
	 *
	 * @param writable 即将写入的字节数
	 * @return 是否可写入
	 */
	boolean ensureWritable(int writable);

	/**
	 * 缓冲区长度
	 *
	 * @return 缓冲区长度
	 */
	int length();

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
	void writeValueWithQuote(char value);

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
	 * 写入字符串
	 *
	 * @param value 即将写入的值
	 */
	void writeValueWithQuote(String value);

	/**
	 * 写入boolean值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, boolean value);

	/**
	 * 写入char值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, char value);

	/**
	 * 写入int值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param length    值长度
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, int length, int value);

	/**
	 * 写入long值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param length    值长度
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, int length, long value);

	/**
	 * 写入string值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, String value);

	/**
	 * 写入object值
	 *
	 * @param mark      分隔符
	 * @param fieldName 字段名
	 * @param value     字段值
	 */
	void writeValue(byte mark, IFieldName fieldName, Object value);

	/**
	 * 写入对象结尾
	 * <p>
	 * {@code '}'}表示对象结尾
	 */
	void writeEndObject();

	/**
	 * 写入数组结尾
	 * <p>
	 * {@code ']'}表示数组结尾
	 */
	void writeEndArray();

	/**
	 * 写入{@code null}字符串
	 */
	void writeNull();
}