package com.keimons.deepjson.serializer;

public class StringSerializer implements ISerializer {


	@Override
	public int length(Object object, long options) {
		String value = (String) object;
		return value.length();
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		String value = (String) object;
		buf.writeString(value);
	}
}