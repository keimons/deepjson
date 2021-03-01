package com.keimons.deepjson.serializer;

public interface IFieldName {

	byte coder();

	int length();

	long offset();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();
}