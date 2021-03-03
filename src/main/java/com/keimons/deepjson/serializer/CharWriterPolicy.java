package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * char类型写入策略
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
class CharWriterPolicy implements IWriterStrategy {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final char[] BOOLEAN_TRUE_CHAR = {'t', 'r', 'u', 'e'};

	private static final char[] BOOLEAN_FALSE_CHAR = {'f', 'a', 'l', 's', 'e'};

	private final long options;

	private char[] buf;

	private int writeIndex;

	public CharWriterPolicy(long options, char[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
	}

	@Override
	public final int writeIndex() {
		return writeIndex;
	}

	@Override
	public void writeMark(char mark) {
		buf[writeIndex++] = mark;
	}

	@Override
	public final void writeValue(char value) {
		buf[writeIndex++] = '"';
		buf[writeIndex++] = value;
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
			buf[--position] = (char) SerializerUtil.DigitOnes[r];
			buf[--position] = (char) SerializerUtil.DigitTens[r];
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

	@Override
	public void writeValue(String value) {
		char[] bytes = (char[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
		writeIndex += bytes.length;
	}

	// always private
	private void writeValue(byte mark, char[] fieldName) {
		buf[writeIndex++] = (char) mark;
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValue(mark, fieldName.getFieldNameByChar());
		if (value) {
			System.arraycopy(BOOLEAN_TRUE_CHAR, 0, buf, writeIndex, 4);
			writeIndex += 4;
		} else {
			System.arraycopy(BOOLEAN_FALSE_CHAR, 0, buf, writeIndex, 5);
			writeIndex += 5;
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValue(mark, fieldName.getFieldNameByChar());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValue(mark, fieldName.getFieldNameByChar());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValue(mark, fieldName.getFieldNameByChar());
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
			buf[--position] = (char) SerializerUtil.DigitOnes[r];
			buf[--position] = (char) SerializerUtil.DigitTens[r];
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			buf[--position] = (char) SerializerUtil.DigitOnes[r];
			buf[--position] = (char) SerializerUtil.DigitTens[r];
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		buf[--position] = (char) ('0' + r);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			buf[--position] = (char) ('0' - q2);
		}

		if (negative) {
			buf[--position] = '-';
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValue(mark, fieldName.getFieldNameByChar());
		char[] stringChars = value.toCharArray();
		int length = stringChars.length;
		System.arraycopy(stringChars, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValue(mark, fieldName.getFieldNameByChar());
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