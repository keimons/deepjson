package com.keimons.deepjson.util;

import com.keimons.deepjson.CodecConfig;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 编解码工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CodecUtil {

	/**
	 * 是否使用{@code char}作为{@link String}内部编码
	 */
	public static final boolean CHARS;
	/**
	 * 在低版本jdk中{@code java.lang.String#values}字段相对首地址偏移位置。
	 * <p>
	 * 该字段仅在jdk1.8以及以下的版本中才存在。
	 */
	public static final long VALUES_OFFSET_STRING;
	/**
	 * 编码标识符 单字节
	 */
	public static final byte LATIN1;
	/**
	 * 编码标识符 双字节
	 */
	public static final byte UTF16;
	public static final long VALUE_OFFSET_STRING;

	// region jdk 8-
	public static final long CODER_OFFSET_STRING;
	/**
	 * 非常诡异的是，Java中几乎都采用了大端序，但是，在Java 9的String内部
	 * 编码{@code java.lang.String#value}，却采用了小端序。
	 * <p>
	 * char类型占用两个byte，低8位在前，高8位在后
	 */
	public static final int HI_BYTE_SHIFT; // 0
	public static final int LO_BYTE_SHIFT; // 8
	// endregion

	// region jdk 9+
	public static final boolean BIG_ENCODE; // 8
	public static final String CLASS_STRING_UTF16 = "java.lang.StringUTF16";
	public static final char[] CHAR_HEX = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	public static final char[] DigitOnes = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	};
	public static final char[] DigitTens = {
			'0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
			'1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
			'2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
			'3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
			'4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
			'5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
			'6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
			'7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
			'8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
			'9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
	};
	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();
	/**
	 * {@code char}值长度，对于需要转码的值，按照转码后的长度进行计算。
	 */
	private static final int[] REPLACEMENT_LENGTH;
	/**
	 * 是否开启字符串压缩
	 */
	private static final boolean COMPACT_STRINGS;

	static {
		Object value = null;
		try {
			long offset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
			value = unsafe.getObject("DeepJson", offset);
		} catch (NoSuchFieldException e) {
			// ignore
			if (CodecConfig.DEBUG) {
				e.printStackTrace();
			}
		}
		CHARS = value == null || value.getClass() != byte[].class;
	}

	static {
		REPLACEMENT_LENGTH = new int[256];
		Arrays.fill(REPLACEMENT_LENGTH, 1);
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_LENGTH[i] = 6;
		}
		REPLACEMENT_LENGTH['"'] = 2;
		REPLACEMENT_LENGTH['\\'] = 2;
		REPLACEMENT_LENGTH['\t'] = 2;
		REPLACEMENT_LENGTH['\b'] = 2;
		REPLACEMENT_LENGTH['\n'] = 2;
		REPLACEMENT_LENGTH['\r'] = 2;
		REPLACEMENT_LENGTH['\f'] = 2;
		REPLACEMENT_LENGTH[127] = 6;
		for (int i = 129; i <= 159; i++) {
			REPLACEMENT_LENGTH[i] = 6;
		}
	}

	static {
		long values_offset_string = -1;
		try {
			if (PlatformUtil.javaVersion() <= 8) {
				values_offset_string = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
			}
		} catch (NoSuchFieldException e) {
			// TODO 切换安全模式
			throw new UnsupportedException();
		}
		VALUES_OFFSET_STRING = values_offset_string;
	}

	static {
		int hi_byte_shift = 0;
		int lo_byte_shift = 8;
		boolean compact_strings = true;
		byte latin1 = 0;
		byte utf16 = 0;
		long value_offset_string = 0;
		long coder_offset_string = 0;
		if (PlatformUtil.javaVersion() >= 9) {
			try {
				Class<?> clazz = Class.forName(CLASS_STRING_UTF16);
				unsafe.ensureClassInitialized(clazz);
				Field f_hi_byte_shift = clazz.getDeclaredField("HI_BYTE_SHIFT");
				Field f_lo_byte_shift = clazz.getDeclaredField("LO_BYTE_SHIFT");
				long hi_byte_shift_offset = unsafe.staticFieldOffset(f_hi_byte_shift);
				long lo_byte_shift_offset = unsafe.staticFieldOffset(f_lo_byte_shift);
				hi_byte_shift = unsafe.getInt(clazz, hi_byte_shift_offset);
				lo_byte_shift = unsafe.getInt(clazz, lo_byte_shift_offset);

				Field field = String.class.getDeclaredField("COMPACT_STRINGS");
				long offset = unsafe.staticFieldOffset(field);
				compact_strings = unsafe.getBoolean(String.class, offset);
				field = String.class.getDeclaredField("LATIN1");
				offset = unsafe.staticFieldOffset(field);
				latin1 = unsafe.getByte(String.class, offset);
				field = String.class.getDeclaredField("UTF16");
				offset = unsafe.staticFieldOffset(field);
				utf16 = unsafe.getByte(String.class, offset);

				value_offset_string = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
				coder_offset_string = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
			} catch (Exception e) {
				// ignore 确保 java.lang.StringUTF16 已经被初始化
				// ignore default 低8位在前 高8位在后
				// TODO 切换安全模式
				throw new UnsupportedException();
			}
		}
		HI_BYTE_SHIFT = hi_byte_shift;
		LO_BYTE_SHIFT = lo_byte_shift;
		BIG_ENCODE = HI_BYTE_SHIFT != 0 || LO_BYTE_SHIFT != 8;
		COMPACT_STRINGS = compact_strings;
		LATIN1 = latin1;
		UTF16 = utf16;
		VALUE_OFFSET_STRING = value_offset_string;
		CODER_OFFSET_STRING = coder_offset_string;
	}
	// endregion

	/**
	 * 计算字符串的长度
	 *
	 * @param object 对象
	 * @return 字符串长度
	 */
	private static int length8(String object) {
		int length = 0;
		char[] values = (char[]) unsafe.getObject(object, CodecUtil.VALUES_OFFSET_STRING);
		for (char value : values) {
			if (value < 256) {
				length += CodecUtil.REPLACEMENT_LENGTH[value & 0xFF];
			} else {
				if (value == 0x2028 || value == 0x2029) {
					length += 6;
				} else {
					length++;
				}
			}
		}
		return length;
	}

	/**
	 * 判断是否单字节编码
	 *
	 * @param coder 编码标识
	 * @return 是否单字节编码
	 */
	public static boolean isLatin1(byte coder) {
		return COMPACT_STRINGS && coder == LATIN1;
	}

	/**
	 * 计算字符串的长度
	 *
	 * @param object 对象
	 * @return 字符串长度
	 */
	private static int length9(String object) {
		int length = 0;
		byte coder = unsafe.getByte(object, CodecUtil.CODER_OFFSET_STRING);
		byte[] values = (byte[]) unsafe.getObject(object, CodecUtil.VALUE_OFFSET_STRING);
		if (isLatin1(coder)) {
			for (byte value : values) {
				length += REPLACEMENT_LENGTH[value & 0xFF];
			}
		} else {
			int i = CodecUtil.BIG_ENCODE ? 0 : 1;
			int j = CodecUtil.BIG_ENCODE ? 1 : 0;
			for (; i < values.length; i += 2, j += 2) {
				byte hi = values[i];
				byte lo = values[j];
				if (lo == 0) {
					length += REPLACEMENT_LENGTH[hi & 0xFF];
				} else if (lo == 0x20 && (hi == 0x28 || hi == 0x29)) {
					length += 6;
				} else {
					length++;
				}
			}
		}
		return length;
	}

	/**
	 * 计算{@code int}值字符串长度
	 *
	 * @param value {@code int}值
	 * @return 字符串长度
	 */
	public static int length(int value) {
		int d = 1;
		if (value >= 0) {
			d = 0;
			value = -value;
		}
		int p = -10;
		for (int i = 1; i < 10; i++) {
			if (value > p)
				return i + d;
			p = 10 * p;
		}
		return 10 + d;
	}

	/**
	 * 计算{@code long}值字符串长度
	 *
	 * @param value {@code long}值
	 * @return 字符串长度
	 */
	public static int length(long value) {
		int d = 1;
		if (value >= 0) {
			d = 0;
			value = -value;
		}
		long p = -10;
		for (int i = 1; i < 19; i++) {
			if (value > p)
				return i + d;
			p = 10 * p;
		}
		return 19 + d;
	}

	/**
	 * 计算{@code char}值的字符串长度
	 *
	 * @param value {@code char}值
	 * @return 字符串长度
	 */
	public static int length(char value) {
		if (value < 256) {
			return REPLACEMENT_LENGTH[value];
		} else if (value == 0x2028 || value == 0x2029) {
			return 6;
		} else {
			return 1;
		}
	}

	/**
	 * 计算{@link String}值的字符串长度
	 *
	 * @param value {@link String}值
	 * @return 字符串长度
	 */
	public static int length(String value) {
		return CHARS ? length8(value) : length9(value);
	}

	/**
	 * 向缓冲区中写入一个{@code int}值
	 *
	 * @param buf        缓冲区
	 * @param writeIndex 写入位置
	 * @param value      {@code int}值
	 */
	public static void writeInt(char[] buf, int writeIndex, int value) {
		int q, r;
		int position = writeIndex;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using ints
		while (value <= -100) {
			q = value / 100;
			r = (q * 100) - value;
			value = q;
			buf[--position] = DigitOnes[r];
			buf[--position] = DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		buf[--position] = (char) ('0' + r);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--position] = (char) ('0' - q);
		}

		if (negative) {
			buf[--position] = '-';
		}
	}

	/**
	 * 向缓冲区中写入一个{@code long}值
	 *
	 * @param buf        缓冲区
	 * @param writeIndex 写入位置
	 * @param value      {@code long}值
	 */
	public static void writeLong(char[] buf, int writeIndex, long value) {
		long q;
		int r;
		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (value <= Integer.MIN_VALUE) {
			q = value / 100;
			r = (int) ((q * 100) - value);
			value = q;
			buf[--writeIndex] = DigitOnes[r];
			buf[--writeIndex] = DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			buf[--writeIndex] = DigitOnes[r];
			buf[--writeIndex] = DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		buf[--writeIndex] = (char) ('0' + r);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			buf[--writeIndex] = (char) ('0' - q2);
		}

		if (negative) {
			buf[--writeIndex] = '-';
		}
	}

	/**
	 * 按照十进制在缓冲区中读取{@code int}值
	 *
	 * @param buf    缓冲区
	 * @param start  起始位置
	 * @param length 读取长度
	 * @return {@code int}值
	 * @throws NumberFormatException 数字格式异常
	 */
	public static int readInt(char[] buf, int start, int length) {
		if (length <= 0) {
			throw new NumberFormatException("For input string: \"\"");
		}
		int index = start;
		int c = buf[index];
		boolean negative = c == '-';
		if (c == '-' || c == '+') {
			index++;
		}
		if (index == length) { // only "+" or "-"
			String msg = "Error at index " + index + " in: \"" + new String(buf, 0, length) + "\"";
			throw new NumberFormatException(msg);
		}
		int limit = negative ? Integer.MIN_VALUE : -Integer.MAX_VALUE;
		long multi = limit / 10;
		int result = 0;
		while (index < length) {
			c = buf[index++];
			int digit = ((c < 48) || (57 < c)) ? -1 : c - 48;
			if (digit < 0 || result < multi) {
				throw new NumberFormatException("For input string: \"" + new String(buf, 0, length) + "\"");
			}
			result *= 10;
			if (result < limit + digit) {
				throw new NumberFormatException("For input string: \"" + new String(buf, 0, length) + "\"");
			}
			result -= digit;
		}
		return negative ? result : -result;
	}

	/**
	 * 按照十进制在缓冲区中读取{@code long}值
	 *
	 * @param buf    缓冲区
	 * @param start  起始位置
	 * @param length 读取长度
	 * @return {@code long}值
	 * @throws NumberFormatException 数字格式异常
	 */
	public static long readLong(char[] buf, int start, int length) {
		if (length <= 0) {
			throw new NumberFormatException("For input string: \"\"");
		}
		int index = start;
		int c = buf[index];
		boolean negative = c == '-';
		if (c == '-' || c == '+') {
			index++;
		}
		if (index == length) { // only "+" or "-"
			String msg = "Error at index " + index + " in: \"" + new String(buf, 0, length) + "\"";
			throw new NumberFormatException(msg);
		}
		long limit = negative ? Long.MIN_VALUE : -Long.MAX_VALUE;
		long multi = limit / 10;
		long result = 0;
		while (index < length) {
			c = buf[index++];
			int digit = ((c < 48) || (57 < c)) ? -1 : c - 48;
			if (digit < 0 || result < multi) {
				throw new NumberFormatException("For input string: \"" + new String(buf, 0, length) + "\"");
			}
			result *= 10;
			if (result < limit + digit) {
				throw new NumberFormatException("For input string: \"" + new String(buf, 0, length) + "\"");
			}
			result -= digit;
		}
		return negative ? result : -result;
	}
}