package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

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

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, boolean value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, char value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, byte value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, short value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, int value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, long value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, float value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, double value) {
		buf.writeValue(mark, name, value);
		return true;
	}

	static boolean write(WriterBuffer buf, long options, char mark, char[] name, String value) {
		if (value != null || CodecOptions.IgnoreNonField.noOptions(options)) {
			buf.writeValue(mark, name, value);
			return true;
		}
		return false;
	}

	static boolean write(WriterContext context, WriterBuffer buf, long options, char mark, char[] name) {
		if (context.isEmptyHead() && CodecOptions.IgnoreNonField.isOptions(options)) {
			context.poll();
			return false;
		} else {
			buf.writeName(mark, name);
			context.encode(buf, CodecModel.V, options);
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