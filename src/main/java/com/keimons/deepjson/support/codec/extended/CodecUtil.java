package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;

/**
 * 拓展编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
class CodecUtil {

	static boolean write(JsonWriter writer, long options, char mark, char[] name, boolean value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, char value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, byte value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, short value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, int value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, long value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, float value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, double value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	static boolean write(JsonWriter writer, long options, char mark, char[] name, String value) throws IOException {
		if (value != null || CodecOptions.IgnoreNonField.noOptions(options)) {
			writer.writeValue(mark, name, value);
			return true;
		}
		return false;
	}

	static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name) throws IOException {
		if (context.isEmptyHead() && CodecOptions.IgnoreNonField.isOptions(options)) {
			context.poll();
			return false;
		} else {
			writer.writeName(mark, name);
			context.encode(writer, CodecModel.V, options);
			return true;
		}
	}

	static void read(Object instance, ReaderBuffer buf, ReaderContext context, long options) {
		int value = buf.intValue();
		context.put(value, instance);
	}

	static void read(Object instance, ReaderBuffer buf, ReaderContext context, long options, Type type, MethodHandle setter) throws Throwable {
		if (buf.token() == SyntaxToken.NULL) {
			setter.invoke(instance, null);
		} else if (buf.is$Id()) {
			context.addCompleteHook(instance, setter, buf.get$Id());
		} else {
			Object value = context.decode(buf, type, options);
			setter.invoke(instance, value);
		}
	}
}