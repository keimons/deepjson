package com.keimons.deepjson.serializer;

import jdk.internal.vm.annotation.ForceInline;

/**
 * 写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
interface IWriterStrategy {

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, boolean value);

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, char value);

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, int length, int value);

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, int length, long value);

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, String value);

	@ForceInline
	void writeValue(byte mark, IFieldName fieldName, Object value);

	@ForceInline
	void writeEndObject();

	@ForceInline
	void writeEndArray();
}