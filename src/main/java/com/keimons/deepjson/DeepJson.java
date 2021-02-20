package com.keimons.deepjson;

import com.keimons.deepjson.serializer.SerializerFactory;
import com.keimons.deepjson.serializer.ISerializer;

public class DeepJson {

	public static String toJsonString(Object object) {
		ISerializer serializer = SerializerFactory.getWriter(object.getClass());
		return serializer.write(object);
	}
}