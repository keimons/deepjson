package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * 压缩字符串写入
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class LatinWriterPolicy implements IWriterStrategy {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

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
		if (value) {
			System.arraycopy(BOOLEAN_TRUE_LATIN, 0, buf, writeIndex, 4);
			writeIndex += 4;
		} else {
			System.arraycopy(BOOLEAN_FALSE_LATIN, 0, buf, writeIndex, 5);
			writeIndex += 5;
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
		buf[writeIndex++] = '"';
		buf[writeIndex++] = (byte) value;
		buf[writeIndex++] = '"';
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
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

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValue(mark, fieldName.getFieldNameByLatin());
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

	@ForceInline
	@Override
	public final int writeIndex() {
		return writeIndex;
	}
}