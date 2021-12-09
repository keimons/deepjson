package com.keimons.deepjson;

import com.keimons.deepjson.internal.LocalCache;
import com.keimons.deepjson.support.DefaultReader;
import com.keimons.deepjson.support.context.TypeAndHookContext;
import com.keimons.deepjson.util.WriteFailedException;

import java.io.IOException;
import java.lang.reflect.Type;

public class DeepJson {

	/**
	 * 线程本地缓存
	 */
	private static final ThreadLocal<LocalCache> CACHE = new ThreadLocal<LocalCache>();

	// empty options
	private static final CodecOptions[] EMPTY = new CodecOptions[0];

	private static LocalCache getLocalCache() {
		LocalCache config = CACHE.get();
		if (config == null) {
			config = new LocalCache();
			CACHE.set(config);
		}
		return config;
	}

	public static String toJsonString(Object object) {
		return toJsonString(object, 0L);
	}

	public static String toJsonString(Object object, CodecOptions... options) {
		long option = CodecOptions.getOptions(options);
		return toJsonString(object, option);
	}

	public static byte[] toJsonBytes(Object object) {
		return toJsonBytes(object, 0L);
	}

	public static byte[] toJsonBytes(Object object, CodecOptions... options) {
		long option = CodecOptions.getOptions(options);
		return toJsonBytes(object, option);
	}

	public static char[] toJsonChars(Object object) {
		return toJsonChars(object, 0L);
	}

	public static char[] toJsonChars(Object object, CodecOptions... options) {
		long option = CodecOptions.getOptions(options);
		return toJsonChars(object, option);
	}

	public static <T> T writeTo(Object object, Generator<T> writer, T dest) throws IOException {
		return writeTo(object, writer, dest, EMPTY);
	}

	public static <T> T writeTo(Object object, Generator<T> gen, T dest, CodecOptions... options) throws IOException {
		long option = CodecOptions.getOptions(options);
		LocalCache cache = getLocalCache();
		WriterContext context = cache.context;
		JsonWriter writer = cache.writer;
		Buffer buf = cache.buf;
		return encode(object, context, writer, buf, gen, dest, option);
	}

	public static <T> T encode(Object value, WriterContext context, JsonWriter writer, Buffer buf, Generator<T> gen, CodecOptions... options) throws WriteFailedException, IOException {
		long option = CodecOptions.getOptions(options);
		return encode(value, context, writer, buf, gen, null, option);
	}

	static String toJsonString(Object object, long options) {
		LocalCache cache = getLocalCache();
		WriterContext context = cache.context;
		JsonWriter writer = cache.writer;
		Buffer buf = cache.buf;
		Generator<String> gen = cache.gen;
		try {
			return encode(object, context, writer, buf, gen, null, options);
		} catch (WriteFailedException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static byte[] toJsonBytes(Object object, long options) {
		LocalCache cache = getLocalCache();
		WriterContext context = cache.context;
		JsonWriter writer = cache.writer;
		Buffer buf = cache.buf;
		try {
			return encode(object, context, writer, buf, Generator.GENERATOR_BYTES_UTF8, null, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static char[] toJsonChars(Object object, long options) {
		LocalCache cache = getLocalCache();
		WriterContext context = cache.context;
		JsonWriter writer = cache.writer;
		Buffer buf = cache.buf;
		try {
			return encode(object, context, writer, buf, Generator.CHAR_ARRAY, null, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static <T> T encode(Object value, WriterContext context, JsonWriter writer, Buffer buf, Generator<T> gen, T dest, long options) throws WriteFailedException, IOException {
		try {
			writer.init(buf, options);
			context.build(value);
			context.encode(writer, CodecModel.V, options);
			return buf.writeTo(gen, dest, 0);
		} finally {
			context.release(writer);
			buf.close();
		}
	}

	public static Json parseObject(String json) {
		return parseObject(json, (Type) Json.class);
	}

	public static <T> T parseObject(String json, Class<T> clazz) {
		return parseObject(json, (Type) clazz);
	}


	public static <T> T parseObject(String json, Type type) {
		ReaderContext context = new TypeAndHookContext();
		DefaultReader buf = new DefaultReader(json);
		buf.nextToken();
		buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
		T value = context.decode(buf, type, 0L);
		context.runCompleteHooks();
		context.close(buf);
		return value;
	}
}