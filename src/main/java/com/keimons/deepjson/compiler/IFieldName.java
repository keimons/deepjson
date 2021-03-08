package com.keimons.deepjson.compiler;

public interface IFieldName {

	int length();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();

	char[] getFieldNameByChar();
}