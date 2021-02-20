package com.keimons.deepjson;

import com.keimons.deepjson.serializer.SerializerFactory;
import com.keimons.deepjson.serializer.ISerializer;

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
}