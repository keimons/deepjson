package com.keimons.deepjson.filler;

public interface IFieldName {

	byte coder();

	int length();

	long offset();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();
}