package com.keimons.deepjson.serializer;

import com.keimons.deepjson.filler.IFieldName;

/**
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
interface IWriter<T> {

	int writeStartObject(T buf, long options, int writeIndex);

	/**
	 * 写入对象结尾标识。
	 *
	 * @return 写入字符数量
	 */
	int writeEndObject(T buf, long options, int writeIndex);

	int writeStartArray(T buf, long options, int writeIndex);

	/**
	 * 写入数组结尾标识。
	 *
	 * @return 写入字符数量
	 */
	int writeEndArray(T buf, long options, int writeIndex);

	int writeMark(byte[] buf, long options, int writeIndex);

	int writeNull(T buf, long options, int writeIndex);

	int writeField(byte[] buf, long options, int writeIndex, IFieldName filler);

	int writeBoolean(byte[] buf, long options, int writeIndex, boolean value);

	int writeChar(byte[] buf, long options, int writeIndex, char value);

	int writeInt(byte[] buf, long options, int writeIndex, int value);

	int writeLong(byte[] buf, long options, int writeIndex, long value);

	int writeString(byte[] buf, long options, int writeIndex, String value);

	int writeStringWithMark(byte[] buf, long options, int writeIndex, String value);

	int writeInts(byte[] buf, long options, int writeIndex, int[] values);

	int writeInts(byte[] buf, long options, int writeIndex, Integer[] values);
}