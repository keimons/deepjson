package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * 膨胀字节写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class LittleEndianUtf16WriterPolicy implements IWriterStrategy {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_R_BRACES = (byte) ('}' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACES = (byte) ('}' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK = (byte) ('"' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK = (byte) ('"' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_NEGATIVE = (byte) ('-' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_NEGATIVE = (byte) ('-' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_L = (byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L = (byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_U = (byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_U = (byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte[][] REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new byte[256][];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = new byte[]{
					(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i >> 4 & 0xF] >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i >> 4 & 0xF] >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i & 0xF] >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i & 0xF] >> SerializerUtil.LO_BYTE_SHIFT)
			};
		}
		REPLACEMENT_CHARS['"'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('\"' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\"' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\\'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\t'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('t' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('t' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\b'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('b' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('b' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\n'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('n' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('n' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\r'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('r' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('r' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		REPLACEMENT_CHARS['\f'] = new byte[]{
				(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
				(byte) ('f' >> SerializerUtil.HI_BYTE_SHIFT),
				(byte) ('f' >> SerializerUtil.LO_BYTE_SHIFT)
		};
		for (int i = 127; i <= 159; i++) {
			REPLACEMENT_CHARS[i] = new byte[]{
					(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i >> 4 & 0xF] >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i >> 4 & 0xF] >> SerializerUtil.LO_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i & 0xF] >> SerializerUtil.HI_BYTE_SHIFT),
					(byte) (SerializerUtil.BYTE_HEX[i & 0xF] >> SerializerUtil.LO_BYTE_SHIFT)
			};
		}
	}

	byte[] REPLACEMENT_2028 = new byte[]{
			(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('8' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('8' >> SerializerUtil.LO_BYTE_SHIFT)
	};
	byte[] REPLACEMENT_2029 = new byte[]{
			(byte) ('\\' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('\\' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('0' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('0' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('2' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('9' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('9' >> SerializerUtil.LO_BYTE_SHIFT)
	};

	private static final byte[] BOOLEAN_TRUE_UTF16 = {
			(byte) ('t' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('t' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('r' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('r' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('e' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('e' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	private static final byte[] BOOLEAN_FALSE_UTF16 = {
			(byte) ('f' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('f' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('a' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('a' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('s' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('s' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('e' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('e' >> SerializerUtil.LO_BYTE_SHIFT),
	};

	private static final byte[] NULL = {
			(byte) ('n' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('n' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('u' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('l' >> SerializerUtil.LO_BYTE_SHIFT)
	};

	private final long options;

	/**
	 * 缓冲区
	 */
	private byte[] buf;

	private int writeIndex;

	public LittleEndianUtf16WriterPolicy(long options, byte[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
	}

	/**
	 * 存入两个字节
	 * <p>
	 * 当前文件采用小端序，{@code lo == 0}表示单字节编码。
	 *
	 * @param hi 高位
	 * @param lo 低位
	 */
	private void putValue(byte hi, byte lo) {
		if (lo == 0) {
			byte[] bytes = REPLACEMENT_CHARS[hi & 0xFF]; // modify 0 to 255
			if (bytes == null) {
				unsafe.putByte(buf, offset + writeIndex++, hi);
				unsafe.putByte(buf, offset + writeIndex++, lo);
			} else {
				System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
				writeIndex += bytes.length;
			}
		} else if (lo == 0x20 && (hi == 0x28 || hi == 0x29)) {
			if (hi == 0x28) {
				System.arraycopy(REPLACEMENT_2028, 0, buf, writeIndex, 12);
			} else {
				System.arraycopy(REPLACEMENT_2029, 0, buf, writeIndex, 12);
			}
			writeIndex += 12;
		} else {
			// 获取第三个元素
			unsafe.putByte(buf, offset + writeIndex++, hi);
			unsafe.putByte(buf, offset + writeIndex++, lo);
		}
	}

	@Override
	public void setBuf(Object object) {
		buf = (byte[]) object;
	}

	@Override
	public final int writeIndex() {
		return writeIndex;
	}

	@Override
	public final void writeMark(char mark) {
		unsafe.putByte(buf, offset + writeIndex++, (byte) (mark >> SerializerUtil.HI_BYTE_SHIFT));
		unsafe.putByte(buf, offset + writeIndex++, (byte) (mark >> SerializerUtil.LO_BYTE_SHIFT));
	}

	@Override
	public final void writeValue(boolean value) {
		if (value) {
			System.arraycopy(BOOLEAN_TRUE_UTF16, 0, buf, writeIndex, 8);
			writeIndex += 8;
		} else {
			System.arraycopy(BOOLEAN_FALSE_UTF16, 0, buf, writeIndex, 10);
			writeIndex += 10;
		}
	}

	@ForceInline
	@Override
	public final void writeValue(char value) {
		unsafe.putByte(buf, offset + writeIndex++, HI_BYTE_MARK);
		unsafe.putByte(buf, offset + writeIndex++, LO_BYTE_MARK);
		byte hi = (byte) (value >> SerializerUtil.HI_BYTE_SHIFT);
		byte lo = (byte) (value >> SerializerUtil.LO_BYTE_SHIFT);
		putValue(hi, lo);
		unsafe.putByte(buf, offset + writeIndex++, HI_BYTE_MARK);
		unsafe.putByte(buf, offset + writeIndex++, LO_BYTE_MARK);
	}

	@Override
	public final void writeValue(int length, int value) {
		this.writeIndex += (length << 1);
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
			byte ones = SerializerUtil.DigitOnes[r];
			byte tens = SerializerUtil.DigitTens[r];
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.HI_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		byte b = (byte) ('0' + r);
		unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.LO_BYTE_SHIFT));
		unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.HI_BYTE_SHIFT));

		// Whatever left is the remaining digit.
		if (q < 0) {
			b = (byte) ('0' - q);
			unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.HI_BYTE_SHIFT));
		}

		if (negative) {
			unsafe.putByte(buf, offset + --position, LO_BYTE_NEGATIVE);
			unsafe.putByte(buf, offset + --position, HI_BYTE_NEGATIVE);
		}
	}

	@Override
	public void writeValue(int length, long value) {
		this.writeIndex += (length << 1);

		long q;
		int r;
		int position = writeIndex;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (value <= Integer.MIN_VALUE) {
			q = value / 100;
			r = (int) ((q * 100) - value);
			value = q;
			byte ones = SerializerUtil.DigitOnes[r];
			byte tens = SerializerUtil.DigitTens[r];
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.HI_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			byte ones = SerializerUtil.DigitOnes[r];
			byte tens = SerializerUtil.DigitTens[r];
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (ones >> SerializerUtil.HI_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (tens >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		byte b = (byte) ('0' + r);
		unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.LO_BYTE_SHIFT));
		unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.HI_BYTE_SHIFT));

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			b = (byte) ('0' - q2);
			unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.LO_BYTE_SHIFT));
			unsafe.putByte(buf, offset + --position, (byte) (b >> SerializerUtil.HI_BYTE_SHIFT));
		}

		if (negative) {
			unsafe.putByte(buf, offset + --position, LO_BYTE_NEGATIVE);
			unsafe.putByte(buf, offset + --position, HI_BYTE_NEGATIVE);
		}
	}

	@ForceInline
	@Override
	public final void writeValue(String value) {
		byte coder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
		byte[] values = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		if (coder == 0) {
			for (byte b : values) {
				byte[] bytes = REPLACEMENT_CHARS[b];
				if (bytes == null) {
					unsafe.putByte(buf, offset + writeIndex++, (byte) (b >> SerializerUtil.HI_BYTE_SHIFT));
					unsafe.putByte(buf, offset + writeIndex++, (byte) (b >> SerializerUtil.LO_BYTE_SHIFT));
				} else {
					System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
					writeIndex += bytes.length;
				}
			}
		} else {
			for (int i = 0, j = 1; i < values.length; i += 2, j += 2) {
				byte hi = values[i]; // 高8位
				byte lo = values[j]; // 低8位
				putValue(hi, lo);
			}
		}
	}

	@Override
	public final void writeValueWithQuote(String value) {
		unsafe.putByte(buf, offset + writeIndex++, HI_BYTE_MARK);
		unsafe.putByte(buf, offset + writeIndex++, LO_BYTE_MARK);
		writeValue(value);
		unsafe.putByte(buf, offset + writeIndex++, HI_BYTE_MARK);
		unsafe.putByte(buf, offset + writeIndex++, LO_BYTE_MARK);
	}

	// always private
	private void writeValue(byte mark, byte[] fieldName) {
		unsafe.putByte(buf, offset + writeIndex++, (byte) (mark >> SerializerUtil.HI_BYTE_SHIFT));
		unsafe.putByte(buf, offset + writeIndex++, (byte) (mark >> SerializerUtil.LO_BYTE_SHIFT));
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
	}

	@ForceInline
	@Override
	public void writeEndObject() {
		unsafe.putByte(buf, offset + writeIndex++, HI_BYTE_R_BRACES);
		unsafe.putByte(buf, offset + writeIndex++, LO_BYTE_R_BRACES);
	}

	@Override
	public void writeEndArray() {
		unsafe.putByte(buf, offset + writeIndex++, (byte) (']' >> SerializerUtil.HI_BYTE_SHIFT));
		unsafe.putByte(buf, offset + writeIndex++, (byte) (']' >> SerializerUtil.LO_BYTE_SHIFT));
	}

	@Override
	public void writeNull() {
		System.arraycopy(NULL, 0, buf, writeIndex, 8);
		writeIndex += 8;
	}
}