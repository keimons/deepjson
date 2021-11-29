package com.keimons.deepjson;

import com.keimons.deepjson.internal.LocalCache;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.buffer.JsonReaderBuffer;
import com.keimons.deepjson.support.codec.Template;
import com.keimons.deepjson.support.context.TypeAndHookContext;
import com.keimons.deepjson.util.StringGeneratorHelper;
import com.keimons.deepjson.util.WriteFailedException;

import java.lang.reflect.Type;

public class DeepJson {

	/**
	 * 线程本地缓存
	 */
	private static final ThreadLocal<LocalCache> CACHE = new ThreadLocal<LocalCache>();

	// empty options
	private static final CodecOptions[] EMPTY = new CodecOptions[0];

	static {
		// 预热编译器
		String json = DeepJson.toJsonString(new Template());
		if (CodecConfig.DEBUG) {
			System.out.println(json);
		}
		Template template = DeepJson.parseObject(json, Template.class);
		if (CodecConfig.DEBUG) {
			System.out.println(DeepJson.toJsonString(template));
		}
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

	public static <T> T writeTo(Object object, Generator<T> writer, T dest) {
		return writeTo(object, writer, dest, EMPTY);
	}

	public static <T> T writeTo(Object object, Generator<T> writer, T dest, CodecOptions... options) {
		long option = CodecOptions.getOptions(options);
		LocalCache config = CACHE.get();
		if (config == null) {
			config = new LocalCache();
			CACHE.set(config);
		}
		WriterContext context = config.context;
		WriterBuffer buffer = config.buffer;
		return encode(object, context, buffer, writer, dest, option);
	}

	public static <T> T encode(Object value, WriterContext context, WriterBuffer buffer, Generator<T> writer, CodecOptions... options) throws WriteFailedException {
		long option = CodecOptions.getOptions(options);
		return encode(value, context, buffer, writer, null, option);
	}

	static String toJsonString(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		WriterContext context = cache.context;
		WriterBuffer buffer = cache.buffer;
		Generator<String> writer = cache.writer;
		try {
			return encode(object, context, buffer, writer, null, options);
		} catch (WriteFailedException e) {
			if (CodecConfig.DEBUG) {
				e.printStackTrace();
			}
			writer = cache.writer = StringGeneratorHelper.SAFE; // 编码时失败切换安全模式
			return encode(object, context, buffer, writer, null, options);
		}
	}

	static byte[] toJsonBytes(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		WriterContext context = cache.context;
		WriterBuffer buffer = cache.buffer;
		return encode(object, context, buffer, Generator.GENERATOR_BYTES_UTF8, null, options);
	}

	static char[] toJsonChars(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		WriterContext context = cache.context;
		WriterBuffer buffer = cache.buffer;
		return encode(object, context, buffer, Generator.CHAR_ARRAY, null, options);
	}

	static <T> T encode(Object value, WriterContext context, WriterBuffer buf, Generator<T> writer, T dest, long options) throws WriteFailedException {
		buf.init(options);
		try {
			context.build(value);
			context.encode(buf, CodecModel.V, options);
			return buf.writeTo(writer, dest, 0);
		} finally {
			context.release(buf);
		}
	}

	public static <T> T parse(String json) {
		return parseObject(json, (Type) Object.class);
	}

	public static Json parseObject(String json) {
		return parseObject(json, (Type) Json.class);
	}

	public static <T> T parseObject(String json, Class<T> clazz) {
		return parseObject(json, (Type) clazz);
	}


	public static <T> T parseObject(String json, Type type) {
		ReaderContext context = new TypeAndHookContext();
		JsonReaderBuffer buf = new JsonReaderBuffer(json);
		buf.nextToken();
		buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
		T value = context.decode(buf, type, 0L);
		context.runCompleteHooks();
		context.close(buf);
		return value;
	}
}