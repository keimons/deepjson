package com.keimons.deepjson;

public class DeepJson {

	public static String toJsonString(Object object) {
		ISerializer serializer = DeepStringFactory.getWriter(object.getClass());
		return serializer.concat(object);
	}
}