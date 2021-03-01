package com.keimons.deepjson;

import com.keimons.deepjson.serializer.ByteBuf;
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
		long option = SerializerOptions.getOptions(options);
		return toJsonString(object, option);
	}

	private static String toJsonString(Object object, long options) {
		if (object == null) {
			return "null";
		}
		Class<?> clazz = object.getClass();
		ISerializer serializer = SerializerFactory.getSerializer(clazz);
		int capacity = serializer.length(object, options);
		byte coder = serializer.coder(object, options);
		ByteBuf buf = ByteBuf.buffer(options, capacity, coder);
		serializer.write(object, buf);
		return buf.newString();
	}
}