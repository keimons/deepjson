package com.keimons.deepjson.filler;

public interface IFieldName {

	byte[] getFieldNameByUtf16();

	byte[] getFieldNameByLatin();

	int size();
}