package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;

/**
 * 压缩字节写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class LatinWriterPolicy implements IWriterStrategy {

	private static final byte[][] REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new byte[128][];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = new byte[]{
					'\\', 'u', '0', '0', SerializerUtil.BYTE_HEX[i >> 4 & 0xF], SerializerUtil.BYTE_HEX[i & 0xF]
			};
		}
		REPLACEMENT_CHARS['"'] = new byte[]{'\\', '\"'};
		REPLACEMENT_CHARS['\\'] = new byte[]{'\\', '\\'};
		REPLACEMENT_CHARS['\t'] = new byte[]{'\\', 't'};
		REPLACEMENT_CHARS['\b'] = new byte[]{'\\', 'b'};
		REPLACEMENT_CHARS['\n'] = new byte[]{'\\', 'n'};
		REPLACEMENT_CHARS['\r'] = new byte[]{'\\', 'r'};
		REPLACEMENT_CHARS['\f'] = new byte[]{'\\', 'f'};
	}

	private static final byte[] BOOLEAN_TRUE_LATIN = {'t', 'r', 'u', 'e'};

	private static final byte[] BOOLEAN_FALSE_LATIN = {'f', 'a', 'l', 's', 'e'};

	private final long options;

	private byte[] buf;

	private int writeIndex;

	public LatinWriterPolicy(long options, byte[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
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
		buf[writeIndex++] = (byte) mark;
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
	public final void writeValue(char value) {
		buf[writeIndex++] = '"';
		buf[writeIndex++] = (byte) value;
		buf[writeIndex++] = '"';
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
			buf[--position] = SerializerUtil.DigitOnes[r];
			buf[--position] = SerializerUtil.DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		buf[--position] = (byte) ('0' + r);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--position] = (byte) ('0' - q);
		}

		if (negative) {
			buf[--position] = '-';
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
			buf[--position] = SerializerUtil.DigitOnes[r];
			buf[--position] = SerializerUtil.DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			buf[--position] = SerializerUtil.DigitOnes[r];
			buf[--position] = SerializerUtil.DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		buf[--position] = (byte) ('0' + r);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			buf[--position] = (byte) ('0' - q2);
		}

		if (negative) {
			buf[--position] = '-';
		}
	}

	@Override
	public final void writeValue(String value) {
		byte[] bytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
		writeIndex += bytes.length;
	}

	@Override
	public final void writeValueWithMark(String value) {
		buf[writeIndex++] = '"';
		byte[] bytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
		writeIndex += bytes.length;
		buf[writeIndex++] = '"';
	}

	// always private
	@ForceInline
	private void writeValue(byte mark, byte[] fieldName) {
		buf[writeIndex++] = mark;
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
		writeValue(value);
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
		byte[] stringBytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		int length = stringBytes.length;
		System.arraycopy(stringBytes, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
	}

	@ForceInline
	@Override
	public void writeEndObject() {
		buf[writeIndex++] = '}';
	}

	@ForceInline
	@Override
	public void writeEndArray() {
		buf[writeIndex++] = ']';
	}

	@Override
	public void writeNull() {
		buf[writeIndex++] = 'n';
		buf[writeIndex++] = 'u';
		buf[writeIndex++] = 'l';
		buf[writeIndex++] = 'l';
	}
}