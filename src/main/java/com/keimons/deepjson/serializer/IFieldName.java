package com.keimons.deepjson.serializer;

public interface IFieldName {

	int length();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();

	char[] getFieldNameByChar();
}