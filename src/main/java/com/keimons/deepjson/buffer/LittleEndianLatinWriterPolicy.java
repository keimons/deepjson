package com.keimons.deepjson.buffer;

import com.keimons.deepjson.field.IFieldName;
import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;

/**
 * 压缩字节写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class LittleEndianLatinWriterPolicy implements IWriterStrategy {

	private static final byte[][] REPLACEMENT_BYTES;

	static {
		REPLACEMENT_BYTES = new byte[256][];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_BYTES[i] = new byte[]{
					'\\', 'u', '0', '0', SerializerUtil.BYTE_HEX[i >> 4 & 0xF], SerializerUtil.BYTE_HEX[i & 0xF]
			};
		}
		REPLACEMENT_BYTES['"'] = new byte[]{'\\', '\"'};
		REPLACEMENT_BYTES['\\'] = new byte[]{'\\', '\\'};
		REPLACEMENT_BYTES['\t'] = new byte[]{'\\', 't'};
		REPLACEMENT_BYTES['\b'] = new byte[]{'\\', 'b'};
		REPLACEMENT_BYTES['\n'] = new byte[]{'\\', 'n'};
		REPLACEMENT_BYTES['\r'] = new byte[]{'\\', 'r'};
		REPLACEMENT_BYTES['\f'] = new byte[]{'\\', 'f'};

		for (int i = 127; i <= 159; i++) {
			REPLACEMENT_BYTES[i] = new byte[]{
					'\\', 'u', '0', '0', SerializerUtil.BYTE_HEX[i >> 4 & 0xF], SerializerUtil.BYTE_HEX[i & 0xF]
			};
		}
	}

	byte[] REPLACEMENT_2028 = new byte[]{'\\', 'u', '2', '0', '2', '8'};
	byte[] REPLACEMENT_2029 = new byte[]{'\\', 'u', '2', '0', '2', '9'};

	private static final byte[] BOOLEAN_TRUE_LATIN = {'t', 'r', 'u', 'e'};

	private static final byte[] BOOLEAN_FALSE_LATIN = {'f', 'a', 'l', 's', 'e'};

	/**
	 * 序列化选项
	 */
	private final long options;

	/**
	 * 缓冲区
	 */
	private byte[] buf;

	/**
	 * 当前写入位置
	 */
	private int writeIndex;

	/**
	 * 最大写入位置
	 */
	private int maxWriteIndex;

	public LittleEndianLatinWriterPolicy(long options, byte[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
		this.maxWriteIndex = buf.length;
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
		return writable + writeIndex <= maxWriteIndex;
	}

	@Override
	public int length() {
		return maxWriteIndex;
	}

	@Override
	public final void writeMark(char mark) {
		unsafe.putByte(buf, offset + writeIndex++, (byte) mark);
	}

	@Override
	public final void writeValue(boolean value) {
		if (value) {
			System.arraycopy(BOOLEAN_TRUE_LATIN, 0, buf, writeIndex, 4);
			writeIndex += 4;
		} else {
			System.arraycopy(BOOLEAN_FALSE_LATIN, 0, buf, writeIndex, 5);
			writeIndex += 5;
		}
	}

	@Override
	public final void writeValue(int length, int value) {
		this.writeIndex += length;
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
			unsafe.putByte(buf, --position, SerializerUtil.DigitOnes[r]);
			unsafe.putByte(buf, --position, SerializerUtil.DigitTens[r]);
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		unsafe.putByte(buf, --position, (byte) ('0' + r));

		// Whatever left is the remaining digit.
		if (q < 0) {
			unsafe.putByte(buf, --position, (byte) ('0' - q));
		}

		if (negative) {
			unsafe.putByte(buf, --position, (byte) '-');
		}
	}

	@Override
	public void writeValue(int length, long value) {
		this.writeIndex += length;
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
			unsafe.putByte(buf, --position, SerializerUtil.DigitOnes[r]);
			unsafe.putByte(buf, --position, SerializerUtil.DigitTens[r]);
		}
		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			unsafe.putByte(buf, --position, SerializerUtil.DigitOnes[r]);
			unsafe.putByte(buf, --position, SerializerUtil.DigitTens[r]);
		}
		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		unsafe.putByte(buf, --position, (byte) ('0' + r));
		// Whatever left is the remaining digit.
		if (q2 < 0) {
			unsafe.putByte(buf, --position, (byte) ('0' - q2));
		}
		if (negative) {
			unsafe.putByte(buf, --position, (byte) '-');
		}
	}

	@Override
	public final void writeValue(String value) {
		byte coder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
		byte[] values = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		if (coder == 0) {
			for (byte b : values) {
				byte[] bytes = REPLACEMENT_BYTES[b & 0xFF];
				if (bytes == null) {
					unsafe.putByte(buf, offset + writeIndex++, b);
				} else {
					System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
					writeIndex += bytes.length;
				}
			}
		} else { // 0x2028 && 0x2029
			int length = values.length;
			for (int i = 0, j = 1; i < length; i += 2, j += 2) {
				byte hi = values[i];
				byte lo = values[j];
				if (lo == 0) {
					byte[] bytes = REPLACEMENT_BYTES[hi & 0xFF];
					if (bytes == null) {
						unsafe.putByte(buf, offset + writeIndex++, hi);
					} else {
						System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
						writeIndex += bytes.length;
					}
				} else if (lo == 0x20) {
					if (hi == 0x28) {
						System.arraycopy(REPLACEMENT_2028, 0, buf, writeIndex, 6);
					} else if (hi == 0x29) {
						System.arraycopy(REPLACEMENT_2029, 0, buf, writeIndex, 6);
					} else {
						throw new IllegalArgumentException("coder mix error");
					}
					writeIndex += 6;
				} else {
					throw new IllegalArgumentException("coder mix error");
				}
			}
		}
	}

	@Override
	public final void writeValueWithQuote(char value) {
		unsafe.putByte(buf, offset + writeIndex++, (byte) '"');
		if (value == 0x2028) {
			System.arraycopy(REPLACEMENT_2028, 0, buf, writeIndex, 6);
			writeIndex += 6;
		} else if (value == 0x2029) {
			System.arraycopy(REPLACEMENT_2029, 0, buf, writeIndex, 6);
			writeIndex += 6;
		} else {
			byte[] bytes = REPLACEMENT_BYTES[value];
			if (bytes == null) {
				unsafe.putByte(buf, offset + writeIndex++, (byte) value);
			} else {
				System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
				writeIndex += bytes.length;
			}
		}
		unsafe.putByte(buf, offset + writeIndex++, (byte) '"');
	}

	@Override
	public final void writeValueWithQuote(String value) {
		unsafe.putByte(buf, offset + writeIndex++, (byte) '"');
		writeValue(value);
		unsafe.putByte(buf, offset + writeIndex++, (byte) '"');
	}

	// always private
	private void writeValue(byte mark, byte[] fieldName) {
		unsafe.putByte(buf, offset + writeIndex++, mark);
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		writeValueWithQuote(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		byte[] values = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		for (byte b : values) {
			byte[] bytes = REPLACEMENT_BYTES[b];
			if (bytes == null) {
				unsafe.putByte(buf, offset + writeIndex++, b);
			} else {
				System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
				writeIndex += bytes.length;
			}
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
	}

	@ForceInline
	@Override
	public void writeEndObject() {
		unsafe.putByte(buf, offset + writeIndex++, (byte) '}');
	}

	@ForceInline
	@Override
	public void writeEndArray() {
		unsafe.putByte(buf, writeIndex++, (byte) ']');
	}

	@Override
	public void writeNull() {
		unsafe.putByte(buf, offset + writeIndex++, (byte) 'n');
		unsafe.putByte(buf, offset + writeIndex++, (byte) 'u');
		unsafe.putByte(buf, offset + writeIndex++, (byte) 'l');
		unsafe.putByte(buf, offset + writeIndex++, (byte) 'l');
	}
}