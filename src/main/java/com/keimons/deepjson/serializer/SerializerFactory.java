package com.keimons.deepjson.serializer;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SerializerFactory {

	private static final Map<Class<?>, ISerializer> CACHE = new ConcurrentHashMap<>();

	static {
//		CACHE.put(int[].class, new IArraySerializer());
//		CACHE.put(Integer[].class, new IntegerArraySerializer());
	}

	public static ISerializer getSerializer(Class<?> clazz) {
		ISerializer serializer = CACHE.get(clazz);
		if (serializer == null) {
			serializer = CACHE.computeIfAbsent(clazz, cls -> {
				Class<? extends ISerializer> serializerClass = SourceCodeSerializer.instance.findSerializer(clazz);
				try {
					return serializerClass.getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
				return null;
			});
		}
		return serializer;
	}

	protected abstract Class<? extends ISerializer> findSerializer(Class<?> clazz);

	private static final class SourceCodeSerializer extends SerializerFactory {

		private static final SerializerFactory instance = new SourceCodeSerializer();

		private static final String NAME = "$DeepJson";

		private static final String PACKAGE = "$deepjson$.";

		@Override
		protected Class<? extends ISerializer> findSerializer(Class<?> clazz) {
			String className = clazz.getSimpleName() + NAME;
			String packageName = PACKAGE + clazz.getPackageName();
			String source = SourceCodeFactory.create(packageName, className, clazz);
			System.out.println(source);
			return CompilerUtil.compiler(packageName, className, source);
		}
	}
}