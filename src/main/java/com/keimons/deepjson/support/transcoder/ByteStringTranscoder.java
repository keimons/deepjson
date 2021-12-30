package com.keimons.deepjson.support.transcoder;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;
import com.keimons.deepjson.internal.util.LookupUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.util.UnsupportedException;
import com.keimons.deepjson.util.WriteFailedException;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * {@code byte[]}字符串连接工具，适用于jdk 9及以上。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class ByteStringTranscoder implements ITranscoder<String> {

	public static final long VALUE_OFFSET_STRING;

	// region jdk 9+
	public static final long CODER_OFFSET_STRING;
	/**
	 * 是否开启字符串压缩
	 * <p>
	 * jdk9以后默认开启字符串压缩以节省内存。
	 */
	public static final boolean COMPACT_STRINGS;
	public static final String CLASS_STRING_UTF16 = "java.lang.StringUTF16";
	private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
	/**
	 * 字符串压缩句柄
	 * <p>
	 * {@code StringUTF16#compress(char[], int, byte[], int, int)}
	 */
	private static final MethodHandle COMPRESS_HANDLE;

	// region 尝试查找java.lang.StringUTF16方法句柄
	private static final MethodHandle PUT_CHAR_HANDLE;

	static {
		boolean compact_strings = true;
		long value_offset_string = 0;
		long coder_offset_string = 0;
		try {
			Field compactStrings = String.class.getDeclaredField("COMPACT_STRINGS");
			long offset = UNSAFE.staticFieldOffset(compactStrings);
			compact_strings = UNSAFE.getBoolean(String.class, offset);
			value_offset_string = UNSAFE.objectFieldOffset(String.class.getDeclaredField("value"));
			coder_offset_string = UNSAFE.objectFieldOffset(String.class.getDeclaredField("coder"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		COMPACT_STRINGS = compact_strings;
		VALUE_OFFSET_STRING = value_offset_string;
		CODER_OFFSET_STRING = coder_offset_string;
	}

	static {
		MethodHandle compress_handle = null;
		MethodHandle put_char_handle = null;
		try {
			MethodHandles.Lookup lookup = LookupUtil.lookup();
			MethodType type1 = MethodType.methodType(int.class, char[].class, int.class, byte[].class, int.class, int.class);
			compress_handle = lookup.findStatic(Class.forName(CLASS_STRING_UTF16), "compress", type1);
			MethodType type2 = MethodType.methodType(void.class, byte[].class, int.class, int.class);
			put_char_handle = lookup.findStatic(Class.forName(CLASS_STRING_UTF16), "putChar", type2);
		} catch (Exception e) {
			throw new UnsupportedException();
		}
		COMPRESS_HANDLE = compress_handle;
		PUT_CHAR_HANDLE = put_char_handle;
	}

	// endregion

	private static String toString(byte[] value, byte coder) throws InstantiationException {
		String result = (String) UNSAFE.allocateInstance(String.class);
		UNSAFE.putObject(result, VALUE_OFFSET_STRING, value);
		UNSAFE.putByte(result, CODER_OFFSET_STRING, coder);
		return result;
	}

	private static boolean isCompact(char[][] buffers, int bufferIndex, int writeIndex) {
		for (int i = 0; i < bufferIndex; i++) {
			for (char value : buffers[i]) {
				if (value > 0xFF) {
					// double byte
					return false;
				}
			}
		}
		char[] buffer = buffers[bufferIndex];
		for (int i = 0; i < writeIndex; i++) {
			if (buffer[i] > 0xFF) {
				// double byte
				return false;
			}
		}
		return true;
	}

	private static void compressLatin(
			final char[][] buffers, final int bufferIndex, final int writeIndex, final byte[] buf) throws Throwable {
		int index = 0;
		for (int i = 0; i < bufferIndex; i++) {
			// rt: int.class, pt: char[].class, int.class, byte[].class, int.class, int.class
			COMPRESS_HANDLE.invoke(buffers[i], 0, buf, index, buffers[i].length);
			index += buffers[i].length;
		}
		char[] buffer = buffers[bufferIndex];
		// rt: int.class, pt: char[].class, int.class, byte[].class, int.class, int.class
		COMPRESS_HANDLE.invoke(buffer, 0, buf, index, writeIndex);
	}

	private static void compressUtf16(
			final char[][] buffers, final int bufferIndex, final int writeIndex, final byte[] buf) throws Throwable {
		int index = 0;
		for (int i = 0; i < bufferIndex; i++) {
			for (char c : buffers[i]) {
				// rt: void.class, pt: byte[].class, int.class, int.class
				PUT_CHAR_HANDLE.invoke(buf, index++, c);
			}
		}
		char[] buffer = buffers[bufferIndex];
		for (int i = 0; i < writeIndex; i++) {
			// rt: void.class, pt: byte[].class, int.class, int.class
			PUT_CHAR_HANDLE.invoke(buf, index++, buffer[i]);
		}
	}

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		return length;
	}

	@Override
	public String transcoder(char[][] buffers, int length, int bufferIndex, int writerIndex, IConverter<String> converter, String dest, int offset) {
		if (bufferIndex == 0) {
			return new String(buffers[0], 0, writerIndex);
		} else {
			try {
				if (COMPACT_STRINGS) { // 需要进行一次判断，以防止字符串生成失败时造成的内存浪费。
					if (isCompact(buffers, bufferIndex, writerIndex)) {
						byte[] value = new byte[length];
						compressLatin(buffers, bufferIndex, writerIndex, value);
						return toString(value, (byte) 0);
					}
				}
				byte[] value = new byte[length << 1];
				compressUtf16(buffers, bufferIndex, writerIndex, value);
				return toString(value, (byte) 1);
			} catch (Throwable cause) {
				throw new WriteFailedException(cause);
			}
		}
	}
}