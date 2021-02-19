package com.keimons.deepjson;

import com.keimons.deepjson.filler.FillerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeepStringFactory {

	private static final Map<Class<?>, ISerializer> CACHE = new ConcurrentHashMap<>();

	public static ISerializer getWriter(Class<?> clazz) {
		ISerializer serializer = CACHE.get(clazz);
		if (serializer == null) {
			serializer = CACHE.computeIfAbsent(clazz, value -> {
				ISerializer s = new ObjectSerializer();
				try {
					for (Field field : clazz.getFields()) {
						try {
							s.addLast(FillerFactory.create(clazz, field));
						} catch (NoSuchFieldException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					for (Field field : clazz.getDeclaredFields()) {
						try {
							s.addLast(FillerFactory.create(clazz, field));
						} catch (NoSuchFieldException | IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
				return s;
			});
		}
		return serializer;
	}
}