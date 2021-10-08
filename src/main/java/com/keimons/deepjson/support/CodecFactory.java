package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.JsonObject;
import com.keimons.deepjson.compiler.SourceCodeFactory;
import com.keimons.deepjson.support.codec.*;
import com.keimons.deepjson.support.codec.extended.ExtendedCodec;
import com.keimons.deepjson.support.codec.guava.MultimapCodec;
import com.keimons.deepjson.support.codec.guava.TableCodec;
import com.keimons.deepjson.util.CompilerUtil;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.TypeNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;

/**
 * 编解码器工厂
 * <p>
 * 添加VM参数：{@code -Dcom.keimons.deepjson.SourcePath=.}输出编解码方案
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class CodecFactory {

	/**
	 * 第三方拓展编解码方案包路径
	 * <p>
	 * 对于一些非直接支持的类，采用拓展方式生成对应的编解码方案，编解码方案存储于这个包下。
	 */
	private static final String EXTENDED_PACKAGE = "com.keimons.deepjson.support.codec.extended";

	/**
	 * 拓展类名后缀
	 * <p>
	 * 为防止类名重复，采用{@code '$$DeepJson$' + VERSION}表明一个类名。
	 */
	private static final String NAME = "$$DeepJson$";

	/**
	 * 拓展类名后缀
	 * <p>
	 * 为防止类名重复，同名的类采用不同的version进行区分。
	 */
	private static final Map<String, Integer> VERSION = new ConcurrentHashMap<String, Integer>();

	private static final Map<Type, ICodec<?>> CACHE = new ConcurrentHashMap<Type, ICodec<?>>();

	private static final Class<?> GUAVA_TABLE;
	private static final Class<?> GUAVA_MULTIMAP;

	static {
		CACHE.put(Void.class, NullCodec.instance);
		CACHE.put(Class.class, ClassCodec.instance);
		CACHE.put(Object.class, ObjectCodec.instance);
		CACHE.put(String.class, StringCodec.instance);
		CACHE.put(JsonObject.class, MapCodec.instance);
		CACHE.put(TypeVariable.class, TypeVariableCodec.instance);

		CACHE.put(boolean.class, BooleanCodec.instance);
		CACHE.put(Boolean.class, BooleanCodec.instance);
		CACHE.put(boolean[].class, BooleanArrayCodec.instance);

		CACHE.put(byte.class, ByteCodec.instance);
		CACHE.put(Byte.class, ByteCodec.instance);
		CACHE.put(byte[].class, ByteArrayCodec.instance);

		CACHE.put(short.class, ShortCodec.instance);
		CACHE.put(Short.class, ShortCodec.instance);
		CACHE.put(short[].class, ShortArrayCodec.instance);

		CACHE.put(char.class, CharCodec.instance);
		CACHE.put(Character.class, CharCodec.instance);
		CACHE.put(char[].class, CharArrayCodec.instance);

		CACHE.put(int.class, IntegerCodec.instance);
		CACHE.put(Integer.class, IntegerCodec.instance);
		CACHE.put(int[].class, IntegerArrayCodec.instance);

		CACHE.put(long.class, LongCodec.instance);
		CACHE.put(Long.class, LongCodec.instance);
		CACHE.put(long[].class, LongArrayCodec.instance);

		CACHE.put(float.class, FloatCodec.instance);
		CACHE.put(Float.class, FloatCodec.instance);
		CACHE.put(float[].class, FloatArrayCodec.instance);


		CACHE.put(double.class, DoubleCodec.instance);
		CACHE.put(Double.class, DoubleCodec.instance);
		CACHE.put(double[].class, DoubleArrayCodec.instance);

		// jdk 1.8
		if (PlatformUtil.javaVersion() >= 8) {
			CACHE.put(LongAdder.class, LongAdderCodec.instance);
			CACHE.put(DoubleAdder.class, DoubleAdderCodec.instance);
		}

		// Guava
		Class<?> guava_table = null;
		try {
			guava_table = Class.forName("com.google.common.collect.Table");
		} catch (ClassNotFoundException e) {
			// ignore
		}
		GUAVA_TABLE = guava_table;
		Class<?> guava_multimap = null;
		try {
			guava_multimap = Class.forName("com.google.common.collect.Multimap");
		} catch (ClassNotFoundException e) {
			// ignore
		}
		GUAVA_MULTIMAP = guava_multimap;
	}

	public static void put(Class<?> clazz, ICodec<?> codec) {
		CACHE.put(clazz, codec);
	}

	public static <T> ICodec<T> getCodec(T obj) {
		return getCodec(obj == null ? Void.class : obj.getClass());
	}

	/**
	 * 获取编解码器
	 *
	 * @param type 类型
	 * @param <T>  编解码器类型
	 * @return 编解码器
	 */
	@SuppressWarnings("unchecked")
	public static <T> ICodec<T> getCodec(@NotNull Type type) {
		// 常规类型
		if (type instanceof Class<?>) {
			return getCodec((Class<?>) type);
		}

		// 泛型类型
		if (type instanceof ParameterizedType) {
			return (ICodec<T>) ParameterizedTypeCodec.instance;
		}

		// 泛型数组
		if (type instanceof GenericArrayType) {
			return (ICodec<T>) GenericArrayTypeCodec.instance;
		}

		// 泛型参数
		if (type instanceof TypeVariable) {
			return (ICodec<T>) TypeVariableCodec.instance;
		}

		// 通配类型
		if (type instanceof WildcardType) {
			return (ICodec<T>) WildcardTypeCodec.instance;
		}

		throw new TypeNotFoundException("unknown type " + type.getTypeName());
	}

	@SuppressWarnings("unchecked")
	public static <T> ICodec<T> getCodec(@NotNull Class<?> clazz) {
		ICodec<?> codec = CACHE.get(clazz);
		mark:
		if (codec == null) {
			if (clazz.isArray()) {
				codec = ObjectArrayCodec.instance;
			} else {
				if (Collection.class.isAssignableFrom(clazz)) {
					codec = CollectionCodec.instance;
					CACHE.put(clazz, codec);
				} else if (Map.class.isAssignableFrom(clazz)) {
					codec = MapCodec.instance;
					CACHE.put(clazz, codec);
				} else if (Enum.class.isAssignableFrom(clazz)) {
					codec = EnumCodec.instance;
					CACHE.put(clazz, codec);
				} else {
					if (PlatformUtil.javaVersion() >= 8) {
						if (ZoneId.class.isAssignableFrom(clazz)) {
							codec = ZoneIdCodec.instance;
							CACHE.put(clazz, codec);
							break mark;
						}
					}

					// region Google Guava
					// guava table instance
					if (GUAVA_TABLE != null && GUAVA_TABLE.isAssignableFrom(clazz)) {
						codec = TableCodec.instance;
						CACHE.put(clazz, codec);
					}
					// guava multimap instance
					if (GUAVA_MULTIMAP != null && GUAVA_MULTIMAP.isAssignableFrom(clazz)) {
						codec = MultimapCodec.instance;
						CACHE.put(clazz, codec);
					}
					// endregion

					codec = CACHE.get(clazz);
					if (codec == null) {
						// 接口和抽象类不能解码
						if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
							throw new IncompatibleTypeException("interface codec not exist: " + clazz);
						}
						Class<? extends ExtendedCodec> codecClass = SourceCodeCodec.instance.create(clazz);
						try {
							ExtendedCodec instance = codecClass.getDeclaredConstructor().newInstance();
							instance.init(clazz);
							codec = instance;
							CACHE.put(clazz, instance);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return (ICodec<T>) codec;
	}

	/**
	 * 创建编解码方案
	 *
	 * @param clazz 编码目标
	 * @return 编解码方案
	 */
	private Class<? extends ExtendedCodec> create(Class<?> clazz) {
		long startTime = System.nanoTime();
		Class<? extends ExtendedCodec> newClass = create0(clazz);
		if (CodecConfig.DEBUG) {
			System.out.println("Class " + clazz.getName() + "，编译耗时：" + (System.nanoTime() - startTime) / 1000000f);
		}
		return newClass;
	}

	/**
	 * 创建编解码方案
	 *
	 * @param clazz 编码目标
	 * @return 编解码方案
	 */
	protected abstract Class<? extends ExtendedCodec> create0(Class<?> clazz);

	/**
	 * 第三方拓展 源代码编解码工具
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 **/
	private static final class SourceCodeCodec extends CodecFactory {

		/**
		 * 实例化
		 */
		private static final CodecFactory instance = new SourceCodeCodec();

		/**
		 * 源代码输出路径
		 * <p>
		 * 可以通过VM参数{@code -Dcom.keimons.deepjson.SourcePath=.}指定Java文件的输出位置。
		 */
		private static final String SOURCE_PATH;

		static {
			String property = System.getProperty("com.keimons.deepjson.SourcePath");
			String path = null;
			if (property != null && property.length() > 0) {
				if (property.endsWith(File.separator)) {
					path = property;
				} else {
					path = property + File.separator;
				}
				String pkg = EXTENDED_PACKAGE.replaceAll("\\.", Matcher.quoteReplacement(File.separator));
				path += "dump" + File.separator + pkg + File.separator;
			}
			SOURCE_PATH = path;
		}

		@Override
		protected Class<? extends ExtendedCodec> create0(Class<?> clazz) {
			String simpleName = clazz.getSimpleName();
			if (simpleName.contains("/")) {
				// fixed of lambda
				simpleName = simpleName.substring(0, clazz.getSimpleName().indexOf("/"));
			}
			Integer value = VERSION.get(simpleName);
			if (value == null) {
				value = 0;
			}
			int version = value + 1;
			VERSION.put(simpleName, version);
			String className = simpleName + NAME + version;
			String source = SourceCodeFactory.create(EXTENDED_PACKAGE, className, clazz);
			if (SOURCE_PATH != null) {
				File path = new File(SOURCE_PATH);
				if (path.exists() || path.mkdirs()) {
					File file = new File(SOURCE_PATH + className + ".java");
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(file);
						fos.write(source.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (fos != null) {
							try {
								fos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			return CompilerUtil.compiler(EXTENDED_PACKAGE, className, source);
		}
	}
}