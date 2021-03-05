package com.keimons.deepjson.util;

import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class SerializerUtil {

	public static final byte[] BYTE_HEX = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	public static final byte[] DigitOnes = {
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

	public static final byte[] DigitTens = {
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

	public static final byte LATIN = 0;

	public static final byte UTF16 = 1;

	public static final long VALUE_OFFSET_STRING;

	public static final long CODER_OFFSET_STRING;

	/**
	 * 是否开启字符串压缩
	 */
	public static final boolean COMPACT_STRINGS;

	/**
	 * 非常诡异的是，Java中几乎都采用了大端序，但是，在JDK9+的String内部
	 * 编码{@link String#value}中，却采用了小端序。
	 * <p>
	 * char类型占用两个byte，低8位在前，高8位在后
	 */
	public static final int HI_BYTE_SHIFT; // 0
	public static final int LO_BYTE_SHIFT; // 8

	static {
		Class<?> clazz = null;

		try {
			clazz = Class.forName("java.lang.StringUTF16");
			unsafe.ensureClassInitialized(clazz);
		} catch (ClassNotFoundException e) {
			// ignore 确保 java.lang.StringUTF16 已经被初始化
			"蒙奇".getChars(0, 2, new char[2], 0);
		}
		int hi_byte_shift;
		int lo_byte_shift;
		try {
			Field f_hi_byte_shift = clazz.getDeclaredField("HI_BYTE_SHIFT");
			Field f_lo_byte_shift = clazz.getDeclaredField("LO_BYTE_SHIFT");
			long hi_byte_shift_offset = unsafe.staticFieldOffset(f_hi_byte_shift);
			long lo_byte_shift_offset = unsafe.staticFieldOffset(f_lo_byte_shift);
			hi_byte_shift = unsafe.getInt(clazz, hi_byte_shift_offset);
			lo_byte_shift = unsafe.getInt(clazz, lo_byte_shift_offset);
		} catch (NoSuchFieldException e) {
			// ignore default 低8位在前 高8位在后
			hi_byte_shift = 0;
			lo_byte_shift = 8;
		}
		HI_BYTE_SHIFT = hi_byte_shift;
		LO_BYTE_SHIFT = lo_byte_shift;
		boolean compact_strings = true;
		long value_offset_string = 0;
		long coder_offset_string = 0;
		try {
			Field compactStrings = String.class.getDeclaredField("COMPACT_STRINGS");
			long offset = unsafe.staticFieldOffset(compactStrings);
			compact_strings = unsafe.getBoolean(String.class, offset);
			value_offset_string = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
			coder_offset_string = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		COMPACT_STRINGS = compact_strings;
		VALUE_OFFSET_STRING = value_offset_string;
		CODER_OFFSET_STRING = coder_offset_string;
	}

	@ForceInline
	public static int size(int j) {
		int d = 1;
		if (j >= 0) {
			d = 0;
			j = -j;
		}
		int p = -10;
		for (int i = 1; i < 10; i++) {
			if (j > p)
				return i + d;
			p = 10 * p;
		}
		return 10 + d;
	}

	public static int size(long x) {
		int d = 1;
		if (x >= 0) {
			d = 0;
			x = -x;
		}
		long p = -10;
		for (int i = 1; i < 19; i++) {
			if (x > p)
				return i + d;
			p = 10 * p;
		}
		return 19 + d;
	}

	private static final String[] REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
	}

	/**
	 * 计算字符串的宽度
	 *
	 * @param object 对象
	 * @return 字符串长度
	 */
	@ForceInline
	public static int length(String object) {
		int length = 2;
		byte coder = unsafe.getByte(object, SerializerUtil.CODER_OFFSET_STRING);
		byte[] values = (byte[]) unsafe.getObject(object, SerializerUtil.VALUE_OFFSET_STRING);
		if (coder == 0) {
			for (byte value : values) {
				String str = REPLACEMENT_CHARS[value];
				if (str == null) {
					length += 1;
				} else {
					length += str.length();
				}
			}
		} else {
			if (SerializerUtil.LO_BYTE_SHIFT == 8) {
				// 小端序
				for (int i = 1; i < values.length; i += 2) {
					byte value = values[i];
					if (value == 0) {
						String str = REPLACEMENT_CHARS[values[i - 1]];
						if (str == null) {
							length += 1;
						} else {
							length += str.length();
						}
					} else if (value == 0x20 && values[i - 1] == 0x28 || values[i - 1] == 0x29) {
						length += 6;
					} else {
						length++;
					}
				}
			} else {
				// 大端序
				for (int i = 0; i < values.length; i += 2) {
					byte value = values[i];
					if (value == 0) {
						String str = REPLACEMENT_CHARS[values[i + 1]];
						if (str == null) {
							length += 1;
						} else {
							length += str.length();
						}
					} else if (value == 0x20 && values[i + 1] == 0x28 || values[i + 1] == 0x29) {
						length += 6;
					} else {
						length++;
					}
				}
			}
		}
		return length;
	}

//	/**
//	 * float宽度
//	 *
//	 * @param value float值
//	 * @return 长度
//	 */
//	public static int length(float value) {
//		return FloatingDecimal.toJavaFormatLength(value);
//	}
//
//	/**
//	 * double宽度
//	 *
//	 * @param value float值
//	 * @return 长度
//	 */
//	public static int length(double value) {
//		return FloatingDecimal.toJavaFormatLength(value);
//	}
//
//	public static String toJavaFormatString(float value) {
//		return FloatingDecimal.toJavaFormatString(value);
//	}
//
//	public static String toJavaFormatString(double value) {
//		return FloatingDecimal.toJavaFormatString(value);
//	}
}