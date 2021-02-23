package com.keimons.deepjson.serializer;

public interface ISerializerWriter {

	int length(Object object, long options);

	void write(Object object, ByteBuf buf);
}