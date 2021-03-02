package com.keimons.deepjson.serializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SerializerFactory {

	/**
	 * 源代码输出路径
	 */
	private static String DUMP_PATH;

	private static final Map<Class<?>, ISerializer> CACHE = new ConcurrentHashMap<>();

	static {
		String property = System.getProperty("com.keimons.deepjson.DumpPath");
		if (property != null && property.length() > 0) {
			if (property.equals(File.separator)) {
				DUMP_PATH = property;
			} else {
				DUMP_PATH = property + File.separator;
			}
		}
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

		private static final String PACKAGE = "deepjson.";

		@Override
		protected Class<? extends ISerializer> findSerializer(Class<?> clazz) {
			String className = clazz.getSimpleName() + NAME;
			String packageName = PACKAGE + clazz.getPackageName();
			String source = SourceCodeFactory.create(packageName, className, clazz);
			if (DUMP_PATH != null) {
				String filePath = DUMP_PATH + packageName.replaceAll("\\.", File.separator) + File.separator;
				File path = new File(filePath);
				String fileName = className + ".java";
				if (path.exists() || path.mkdirs()) {
					File file = new File(filePath + fileName);
					if (!file.exists()) {

					}
					try (FileOutputStream fos = new FileOutputStream(file)) {
						fos.write(source.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return CompilerUtil.compiler(packageName, className, source);
		}
	}
}