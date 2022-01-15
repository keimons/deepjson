package com.keimons.deepjson.internal.util;

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
public class CodecUtils {

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, boolean value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, char value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, byte value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, short value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, int value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, long value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, float value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, double value) throws IOException {
		writer.writeValue(mark, name, value);
		return true;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, String value) throws IOException {
		if (value != null || CodecOptions.IgnoreNonField.noOptions(options)) {
			writer.writeValue(mark, name, value);
			return true;
		}
		return false;
	}

	public static boolean write(WriterContext context, JsonWriter writer, long options, char mark, char[] name, Object value) throws IOException {
		if (context.isEmptyHead() && CodecOptions.IgnoreNonField.isOptions(options)) {
			context.poll();
			return false;
		} else {
			writer.writeName(mark, name);
			context.encode(writer, CodecModel.V, options);
			return true;
		}
	}

	public static boolean readBoolean(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.booleanValue();
	}

	public static char readCharacter(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.charValue();
	}

	public static byte readByte(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.byteValue();
	}

	public static short readShort(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.shortValue();
	}

	public static int readInteger(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.intValue();
	}

	public static long readLong(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.longValue();
	}

	public static float readFloat(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.floatValue();
	}

	public static double readDouble(ReaderContext context, JsonReader reader, long options, Type type) {
		return reader.doubleValue();
	}

	public static Object readObject(ReaderContext context, JsonReader reader, long options, Type type) {
		if (reader.token() == SyntaxToken.NULL) {
			return null;
		} else {
			return context.decode(reader, type, options);
		}
	}

	// region 使用无参构造方法的实体对象

	public static void readSetId(Object instance, ReaderContext context, JsonReader reader, long options) {
		int value = reader.intValue();
		context.put(value, instance);
	}

	public static void readObject(Object instance, ReaderContext context, JsonReader reader, long options, Type type, MethodHandle setter) throws Throwable {
		if (reader.token() == SyntaxToken.NULL) {
			setter.invoke(instance, null);
		} else if (reader.check$Id()) {
			context.addCompleteHook(instance, setter, reader.get$Id());
		} else {
			Object value = context.decode(reader, type, options);
			setter.invoke(instance, value);
		}
	}
	// endregion

	public static Object read(JsonReader reader, ReaderContext context, long options, Type type) {
		if (reader.token() == SyntaxToken.NULL) {
			return null;
		} else if (reader.check$Id()) {
			Hook hook = new Hook();
			hook.id = reader.get$Id();
			return hook;
		} else {
			return context.decode(reader, type, options);
		}
	}
}