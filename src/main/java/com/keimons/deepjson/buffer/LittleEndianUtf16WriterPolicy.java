package com.keimons.deepjson.buffer;

import com.keimons.deepjson.field.IFieldName;
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

	private static final byte BYTE_R_BRACES = (byte) '}';

	private static final byte BYTE_MARK = (byte) '"';

	private static final byte BYTE_NEGATIVE = (byte) '-';

	private static final byte[][] REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new byte[256][];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = new byte[]{
					'\\', 0, 'u', 0, '0', 0, '0', 0,
					SerializerUtil.BYTE_HEX[i >> 4 & 0xF], 0,
					SerializerUtil.BYTE_HEX[i & 0xF], 0
			};
		}
		REPLACEMENT_CHARS['"'] = new byte[]{'\\', 0, '\"', 0};
		REPLACEMENT_CHARS['\\'] = new byte[]{'\\', 0, '\\', 0};
		REPLACEMENT_CHARS['\t'] = new byte[]{'\\', 0, 't', 0};
		REPLACEMENT_CHARS['\b'] = new byte[]{'\\', 0, 'b', 0};
		REPLACEMENT_CHARS['\n'] = new byte[]{'\\', 0, 'n', 0};
		REPLACEMENT_CHARS['\r'] = new byte[]{'\\', 0, 'r', 0};
		REPLACEMENT_CHARS['\f'] = new byte[]{'\\', 0, 'f', 0};
		for (int i = 127; i <= 159; i++) {
			REPLACEMENT_CHARS[i] = new byte[]{
					'\\', 0, 'u', 0, '0', 0, '0', 0,
					SerializerUtil.BYTE_HEX[i >> 4 & 0xF], 0,
					SerializerUtil.BYTE_HEX[i & 0xF], 0
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

	private static final byte[] TYPE = {
			'/', 0, '*', 0, '@', 0, 't', 0, 'y', 0, 'p', 0, 'e', 0, ':', 0
	};

	private final long options;

	/**
	 * 缓冲区
	 */
	private byte[] buf;

	private int writeIndex;

	private int maxWriteIndex;

	public LittleEndianUtf16WriterPolicy(long options, byte[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
		this.maxWriteIndex = buf.length;
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
				unsafe.putByte(buf, offset + writeIndex, hi);
				// lower is always 0
				writeIndex += 2;
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
	public void setByteBuf(byte[] buf) {
		this.buf = buf;
	}

	@Override
	public byte[] getByteBuf() {
		return buf;
	}

	@Override
	public int writeIndex() {
		return writeIndex;
	}

	@Override
	public boolean ensureWritable(int writable) {
		return (writable << 1) + writeIndex <= maxWriteIndex;
	}

	@Override
	public int length() {
		return maxWriteIndex;
	}

	@Override
	public final void writeMark(char mark) {
		unsafe.putByte(buf, offset + writeIndex, (byte) mark);
		writeIndex += 2;
	}

	@Override
	public void writeType(String type) {
		System.arraycopy(TYPE, 0, buf, writeIndex, 16);
		writeIndex += 16;
		writeValue(type);
		unsafe.putByte(buf, offset + writeIndex, (byte) '*');
		writeIndex += 2;
		unsafe.putByte(buf, offset + writeIndex, (byte) '/');
		writeIndex += 2;
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
	public final void writeValueWithQuote(char value) {
		unsafe.putByte(buf, offset + writeIndex, BYTE_MARK);
		writeIndex += 2;
		byte hi = (byte) (value >> SerializerUtil.HI_BYTE_SHIFT);
		byte lo = (byte) (value >> SerializerUtil.LO_BYTE_SHIFT);
		putValue(hi, lo);
		unsafe.putByte(buf, offset + writeIndex, BYTE_MARK);
		writeIndex += 2;
	}

	@Override
	public final void writeValue(int length, int value) {
		this.writeIndex += length << 1;
		int q, r;
		int position = writeIndex + offset;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using ints
		while (value <= -100) {
			q = value / 100;
			r = (q * 100) - value;
			value = q;
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT));
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		position -= 2;
		unsafe.putByte(buf, position, (byte) (('0' + r) >> SerializerUtil.HI_BYTE_SHIFT));

		// Whatever left is the remaining digit.
		if (q < 0) {
			position -= 2;
			unsafe.putByte(buf, position, (byte) (('0' - q) >> SerializerUtil.HI_BYTE_SHIFT));
		}

		if (negative) {
			position -= 2;
			unsafe.putByte(buf, position, BYTE_NEGATIVE);
		}
	}

	@Override
	public void writeValue(int length, long value) {
		this.writeIndex += length << 1;

		long q;
		int r;
		int position = writeIndex + offset;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using longs until quotient fits into an int
		while (value <= Integer.MIN_VALUE) {
			q = value / 100;
			r = (int) ((q * 100) - value);
			value = q;
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT));
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT));
			position -= 2;
			unsafe.putByte(buf, position, (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT));
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		position -= 2;
		unsafe.putByte(buf, position, (byte) (('0' + r) >> SerializerUtil.HI_BYTE_SHIFT));

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			position -= 2;
			unsafe.putByte(buf, position, (byte) (('0' - q2) >> SerializerUtil.HI_BYTE_SHIFT));
		}

		if (negative) {
			position -= 2;
			unsafe.putByte(buf, position, BYTE_NEGATIVE);
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
					unsafe.putByte(buf, offset + writeIndex, b);
					writeIndex += 2;
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
		unsafe.putByte(buf, offset + writeIndex, BYTE_MARK);
		writeIndex += 2;
		writeValue(value);
		unsafe.putByte(buf, offset + writeIndex, BYTE_MARK);
		writeIndex += 2;
	}

	// always private
	private void writeValueWithQuote(byte mark, byte[] fieldName) {
		unsafe.putByte(buf, offset + writeIndex, mark);
		writeIndex += 2;
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
		writeValueWithQuote(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByUtf16());
	}

	@ForceInline
	@Override
	public void writeEndObject() {
		unsafe.putByte(buf, offset + writeIndex, BYTE_R_BRACES);
		writeIndex += 2;
	}

	@Override
	public void writeEndArray() {
		unsafe.putByte(buf, offset + writeIndex, (byte) ']');
		writeIndex += 2;
	}

	@Override
	public void writeNull() {
		unsafe.putByte(buf, offset + writeIndex, (byte) 'n');
		writeIndex += 2;
		unsafe.putByte(buf, offset + writeIndex, (byte) 'u');
		writeIndex += 2;
		unsafe.putByte(buf, offset + writeIndex, (byte) 'l');
		writeIndex += 2;
		unsafe.putByte(buf, offset + writeIndex, (byte) 'l');
		writeIndex += 2;
	}
}