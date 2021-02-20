package com.keimons.deepjson.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {

	private static final Map<Class<?>, ISerializer> CACHE = new ConcurrentHashMap<>();

	static {
		CACHE.put(int[].class, new IArraySerializer());
		CACHE.put(Integer[].class, new IntegerArraySerializer());
	}

	public static ISerializer getWriter(Class<?> clazz) {
		ISerializer serializer = CACHE.get(clazz);
		if (serializer == null) {
			serializer = CACHE.computeIfAbsent(clazz, value -> new ObjectSerializer(clazz));
		}
		return serializer;
	}
}