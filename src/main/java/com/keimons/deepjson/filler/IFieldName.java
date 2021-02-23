package com.keimons.deepjson.filler;

public interface IFieldName {

	byte coder();

	int length();

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();
}