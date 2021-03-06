package com.keimons.deepjson.serializer;

import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.keimons.deepjson.compiler.SourceCodeFactory;
import com.keimons.deepjson.serializer.support.guava.MultimapSerializer;
import com.keimons.deepjson.serializer.support.guava.RangeMapSerializer;
import com.keimons.deepjson.serializer.support.guava.TableSerializer;
import com.keimons.deepjson.util.CompilerUtil;
import com.keimons.deepjson.util.PlatformUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public abstract class SerializerFactory {

	/**
	 * 源代码输出路径
	 */
	private static String DUMP_PATH;

	private static final Map<Class<?>, ISerializer> CACHE = new ConcurrentHashMap<>();

	private static final WeakHashMap<Class<?>, ISerializer> ARRAY_CACHE = new WeakHashMap<>();

	static {
		String property = System.getProperty("com.keimons.deepjson.DumpPath");
		if (property != null && property.length() > 0) {
			if (property.equals(File.separator)) {
				DUMP_PATH = property;
			} else {
				DUMP_PATH = property + File.separator;
			}
		}
		CACHE.put(Class.class, ClassSerializer.instance);
		CACHE.put(String.class, StringSerializer.instance);

		CACHE.put(boolean.class, BooleanSerializer.instance);
		CACHE.put(Boolean.class, BooleanSerializer.instance);
		CACHE.put(boolean[].class, BooleanArraySerializer.instance);

		CACHE.put(byte.class, ByteSerializer.instance);
		CACHE.put(Byte.class, ByteSerializer.instance);
		CACHE.put(byte[].class, ByteArraySerializer.instance);

		CACHE.put(short.class, ShortSerializer.instance);
		CACHE.put(Short.class, ShortSerializer.instance);
		CACHE.put(short[].class, ShortArraySerializer.instance);

		CACHE.put(char.class, CharSerializer.instance);
		CACHE.put(Character.class, CharSerializer.instance);
		CACHE.put(char[].class, CharArraySerializer.instance);

		CACHE.put(int.class, IntegerSerializer.instance);
		CACHE.put(Integer.class, IntegerSerializer.instance);
		CACHE.put(int[].class, IntegerArraySerializer.instance);

		CACHE.put(long.class, LongSerializer.instance);
		CACHE.put(Long.class, LongSerializer.instance);
		CACHE.put(long[].class, LongArraySerializer.instance);

		CACHE.put(float.class, FloatSerializer.instance);
		CACHE.put(Float.class, FloatSerializer.instance);
		CACHE.put(float[].class, FloatArraySerializer.instance);


		CACHE.put(double.class, DoubleSerializer.instance);
		CACHE.put(Double.class, DoubleSerializer.instance);
		CACHE.put(double[].class, DoubleArraySerializer.instance);

		// jdk 1.8
		if (PlatformUtil.javaVersion() >= 8) {
			CACHE.put(LongAdder.class, LongAdderSerializer.instance);
			CACHE.put(DoubleAdderSerializer.class, LongAdderSerializer.instance);
		}
	}

	public static ISerializer getSerializer(Class<?> clazz) {
		ISerializer serializer = CACHE.get(clazz);
		if (serializer == null) {
			if (clazz.isArray()) {
				serializer = ObjectArraySerializer.instance;
			} else {
				if (Collection.class.isAssignableFrom(clazz)) {
					serializer = CollectionSerializer.instance;
					CACHE.put(clazz, serializer);
				} else if (Map.class.isAssignableFrom(clazz)) {
					serializer = MapSerializer.instance;
					CACHE.put(clazz, serializer);
				} else if (Enum.class.isAssignableFrom(clazz)) {
					serializer = EnumSerializer.instance;
					CACHE.put(clazz, serializer);
				} else {
					// region Google Guava
					try {
						// guava table instance
						if (Table.class.isAssignableFrom(clazz)) {
							serializer = TableSerializer.instance;
							CACHE.put(clazz, serializer);
						}
						// guava multimap instance
						if (Multimap.class.isAssignableFrom(clazz)) {
							serializer = MultimapSerializer.instance;
							CACHE.put(clazz, serializer);
						}
						RangeMapSerializer serializer1 = new RangeMapSerializer();
					} catch (Exception e) {
						// guava not exist, so ignore the exception
					}
					// endregion

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
			}
		}
		return serializer;
	}

	protected abstract Class<? extends ISerializer> findSerializer(Class<?> clazz);

	private static final class SourceCodeSerializer extends SerializerFactory {

		private static final SerializerFactory instance = new SourceCodeSerializer();

		private static final String NAME = "$$DeepJson";

		private static final String PACKAGE = "deepjson.";

		@Override
		protected Class<? extends ISerializer> findSerializer(Class<?> clazz) {
			String simpleName = clazz.getSimpleName();
			if (simpleName.contains("/")) {
				// fixed of lambda
				simpleName = simpleName.substring(0, clazz.getSimpleName().indexOf("/"));
			}
			String className = simpleName + NAME;
			String packageName = PACKAGE + clazz.getPackageName();
			String source = SourceCodeFactory.create(packageName, className, clazz);
			if (DUMP_PATH != null) {
				String filePath = DUMP_PATH + "dump" + File.separator + packageName.replaceAll("\\.", File.separator) + File.separator;
				File path = new File(filePath);
				String fileName = className + ".java";
				if (path.exists() || path.mkdirs()) {
					File file = new File(filePath + fileName);
					if (!file.exists()) {
						if (!file.mkdirs()) {
							System.err.println("目录创建失败");
						}
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