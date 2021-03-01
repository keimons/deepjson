package com.keimons.deepjson.serializer;

import jdk.internal.vm.annotation.ForceInline;

/**
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
interface IWriter<T> {

	@ForceInline
	void writeValue(byte mark, T fieldName, boolean value);

	@ForceInline
	void writeValue(byte mark, T fieldName, char value);

	@ForceInline
	void writeValue(byte mark, T fieldName, int length, int value);

	@ForceInline
	void writeValue(byte mark, T fieldName, int length, long value);

	@ForceInline
	void writeValue(byte mark, T fieldName, String value);

	@ForceInline
	void writeValue(byte mark, T fieldName, Object value);

	@ForceInline
	void writeEndObject();

	@ForceInline
	void writeEndArray();
}