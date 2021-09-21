package com.keimons.deepjson;

import com.keimons.deepjson.support.buffer.JsonReaderBuffer;
import com.keimons.deepjson.support.codec.Template;
import com.keimons.deepjson.support.context.Context;
import com.keimons.deepjson.support.writer.CharsWriter;
import com.keimons.deepjson.support.writer.SafeStringWriter;
import com.keimons.deepjson.support.writer.UTF8BytesWriter;
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
		if (Config.DEBUG) {
			System.out.println(json);
		}
		Template template = DeepJson.parseObject(json, Template.class);
		if (Config.DEBUG) {
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

	public static <T> T writeTo(Object object, AbstractWriter<T> writer) {
		return writeTo(object, writer, EMPTY);
	}

	public static <T> T writeTo(Object object, AbstractWriter<T> writer, CodecOptions... options) {
		long option = CodecOptions.getOptions(options);
		LocalCache config = CACHE.get();
		if (config == null) {
			config = new LocalCache();
			CACHE.set(config);
		}
		AbstractContext context = config.context;
		AbstractBuffer buffer = config.buffer;
		return encode(object, context, buffer, writer, option);
	}

	public static <T> T encode(Object value, AbstractContext context, AbstractBuffer buffer, AbstractWriter<T> writer, CodecOptions... options) throws WriteFailedException {
		long option = CodecOptions.getOptions(options);
		return encode(value, context, buffer, writer, option);
	}

	static String toJsonString(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		AbstractContext context = cache.context;
		AbstractBuffer buffer = cache.buffer;
		AbstractWriter<String> writer = cache.writer;
		try {
			return encode(object, context, buffer, writer, options);
		} catch (WriteFailedException e) {
			if (Config.DEBUG) {
				e.printStackTrace();
			}
			writer = cache.writer = SafeStringWriter.instance; // 编码时失败切换安全模式
			return encode(object, context, buffer, writer, options);
		}
	}

	static byte[] toJsonBytes(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		AbstractContext context = cache.context;
		AbstractBuffer buffer = cache.buffer;
		return encode(object, context, buffer, UTF8BytesWriter.instance, options);
	}

	static char[] toJsonChars(Object object, long options) {
		LocalCache cache = CACHE.get();
		if (cache == null) {
			cache = new LocalCache();
			CACHE.set(cache);
		}
		AbstractContext context = cache.context;
		AbstractBuffer buffer = cache.buffer;
		return encode(object, context, buffer, CharsWriter.instance, options);
	}

	static <T> T encode(Object value, AbstractContext context, AbstractBuffer buf, AbstractWriter<T> writer, long options) throws WriteFailedException {
		buf.init(options);
		try {
			context.build(value);
			context.encode(buf, options);
			return buf.writeTo(writer);
		} finally {
			context.release(buf);
		}
	}

	public static <T> T parse(String json) {
		IDecodeContext context = new Context();
		JsonReaderBuffer buf = new JsonReaderBuffer(json);
		T value = context.decode(buf, Object.class, true, 0L);
		context.runCompleteHooks();
		context.close(buf);
		return value;
	}

	public static <T> T parseObject(String json, Class<T> clazz) {
		IDecodeContext context = new Context();
		JsonReaderBuffer buf = new JsonReaderBuffer(json);
		T value = context.decode(buf, clazz, true, 0L);
		context.runCompleteHooks();
		context.close(buf);
		return value;
	}


	public static <T> T parseObject(String json, Type clazz) {
		IDecodeContext context = new Context();
		JsonReaderBuffer buf = new JsonReaderBuffer(json);
		T value = context.decode(buf, clazz, true, 0L);
		context.runCompleteHooks();
		context.close(buf);
		return value;
	}
}