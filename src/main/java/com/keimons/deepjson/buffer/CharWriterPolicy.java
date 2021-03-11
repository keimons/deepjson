package com.keimons.deepjson.buffer;

import com.keimons.deepjson.field.IFieldName;
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

	private int maxWriteIndex;

	public CharWriterPolicy(long options, char[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
		this.maxWriteIndex = buf.length;
	}

	@Override
	public void setCharBuf(char[] buf) {
		this.buf = buf;
	}

	@Override
	public char[] getCharBuf() {
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
		return buf.length;
	}

	@Override
	public final void writeMark(char mark) {
		buf[writeIndex++] = mark;
	}

	@Override
	public void writeType(String type) {
		buf[writeIndex++] = '/';
		buf[writeIndex++] = '*';
		buf[writeIndex++] = '@';
		buf[writeIndex++] = 't';
		buf[writeIndex++] = 'y';
		buf[writeIndex++] = 'p';
		buf[writeIndex++] = 'e';
		buf[writeIndex++] = ':';
		writeValue(type);
		buf[writeIndex++] = '*';
		buf[writeIndex++] = '/';
	}

	@Override
	public final void writeValue(boolean value) {
		if (value) {
			System.arraycopy(BOOLEAN_TRUE_CHAR, 0, buf, writeIndex, 4);
			writeIndex += 4;
		} else {
			System.arraycopy(BOOLEAN_FALSE_CHAR, 0, buf, writeIndex, 5);
			writeIndex += 5;
		}
	}

	@Override
	public final void writeValueWithQuote(char value) {
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

	@Override
	public void writeValue(String value) {
		char[] bytes = (char[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
		writeIndex += bytes.length;
	}

	@Override
	public void writeValueWithQuote(String value) {
		buf[writeIndex++] = '"';
		char[] bytes = (char[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
		writeIndex += bytes.length;
		buf[writeIndex++] = '"';
	}

	// always private
	private void writeValueWithQuote(byte mark, char[] fieldName) {
		buf[writeIndex++] = (char) mark;
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
		writeValue(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
		writeValueWithQuote(value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
		writeValue(length, value);
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
		char[] stringChars = value.toCharArray();
		int length = stringChars.length;
		System.arraycopy(stringChars, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValueWithQuote(mark, fieldName.getFieldNameByChar());
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