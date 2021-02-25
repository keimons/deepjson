package com.keimons.deepjson;

import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.SerializerFactory;

public class DeepJson {

	private static final SerializerOptions[] EMPTY = new SerializerOptions[0];

	public static String toJsonString(Object object) {
		return toJsonString(object, EMPTY);
	}

	public static String toJsonString(Object object, SerializerOptions... options) {
		if (object == null) {
			return "null";
		}
		ISerializer serializer = SerializerFactory.getWriter(object.getClass());
		return serializer.write(object, options);
	}

	private static String toJsonString(Object object, long options) {
		if (object == null) {
			return "null";
		}

//		int capacity = length(object, option);
//		byte coder = 1;// coder(object, option);
//		ByteBuf buf = ByteBuf.buffer(option, capacity, coder);
//		write(object, buf);
//		return buf.newString();
		return null;
	}
}