package com.keimons.deepjson.field;

public interface IFieldName {

	int length();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();

	char[] getFieldNameByChar();
}