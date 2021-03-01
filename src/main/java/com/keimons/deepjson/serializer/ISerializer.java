package com.keimons.deepjson.serializer;

public interface ISerializer {

	int length(Object object, long options);

	byte coder(Object object, long options);

	void write(Object object, ByteBuf buf);
}