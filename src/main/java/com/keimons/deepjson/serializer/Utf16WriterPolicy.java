package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * JDK9+中未开启字符串压缩
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class Utf16WriterPolicy implements IWriterStrategy {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_R_BRACES = (byte) ('}' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACES = (byte) ('}' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK = (byte) ('"' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK = (byte) ('"' >> SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_NEGATIVE = (byte) ('-' >> SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_NEGATIVE = (byte) ('-' >> SerializerUtil.LO_BYTE_SHIFT);

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

	private final long options;

	private byte[] buf;

	private int writeIndex;

	public Utf16WriterPolicy(long options, byte[] buf, int writeIndex) {
		this.buf = buf;
		this.options = options;
		this.writeIndex = writeIndex;
	}

	// always private
	@ForceInline
	private void writeValue(byte mark, byte[] fieldName) {
		buf[writeIndex++] = (byte) (mark >> SerializerUtil.HI_BYTE_SHIFT);
		buf[writeIndex++] = (byte) (mark >> SerializerUtil.LO_BYTE_SHIFT);
		int length = fieldName.length;
		System.arraycopy(fieldName, 0, buf, writeIndex, length);
		writeIndex += length;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, boolean value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
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
	public void writeValue(byte mark, IFieldName fieldName, char value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
		buf[writeIndex++] = (byte) (value >> SerializerUtil.HI_BYTE_SHIFT);
		buf[writeIndex++] = (byte) (value >> SerializerUtil.LO_BYTE_SHIFT);
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, int value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
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
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		buf[--position] = (byte) ('0' + r >> SerializerUtil.LO_BYTE_SHIFT);
		buf[--position] = (byte) ('0' + r >> SerializerUtil.HI_BYTE_SHIFT);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--position] = (byte) ('0' - q >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) ('0' - q >> SerializerUtil.HI_BYTE_SHIFT);
		}

		if (negative) {
			buf[--position] = HI_BYTE_NEGATIVE;
			buf[--position] = LO_BYTE_NEGATIVE;
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, int length, long value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
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
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
		}

		// Get 2 digits/iteration using ints
		int q2;
		int i2 = (int) value;
		while (i2 <= -100) {
			q2 = i2 / 100;
			r = (q2 * 100) - i2;
			i2 = q2;
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
		}

		// We know there are at most two digits left at this point.
		q2 = i2 / 10;
		r = (q2 * 10) - i2;
		buf[--position] = (byte) ('0' + r >> SerializerUtil.LO_BYTE_SHIFT);
		buf[--position] = (byte) ('0' + r >> SerializerUtil.HI_BYTE_SHIFT);

		// Whatever left is the remaining digit.
		if (q2 < 0) {
			buf[--position] = (byte) ('0' - q2 >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--position] = (byte) ('0' - q2 >> SerializerUtil.HI_BYTE_SHIFT);
		}

		if (negative) {
			buf[--position] = HI_BYTE_NEGATIVE;
			buf[--position] = LO_BYTE_NEGATIVE;
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, String value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
		byte[] stringBytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		byte stringCoder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
		if (stringCoder == SerializerUtil.LATIN) {
			for (byte b : stringBytes) {
				buf[writeIndex++] = (byte) (b >> SerializerUtil.HI_BYTE_SHIFT);
				buf[writeIndex++] = (byte) (b >> SerializerUtil.LO_BYTE_SHIFT);
			}
		} else {
			int length = stringBytes.length;
			System.arraycopy(stringBytes, 0, buf, writeIndex, length);
			writeIndex += length;
		}
	}

	@ForceInline
	@Override
	public void writeValue(byte mark, IFieldName fieldName, Object value) {
		writeValue(mark, fieldName.getFieldNameByUtf16());
	}

	@ForceInline
	@Override
	public void writeEndObject() {
		buf[writeIndex++] = HI_BYTE_R_BRACES;
		buf[writeIndex++] = LO_BYTE_R_BRACES;
	}

	@Override
	public void writeEndArray() {
		buf[writeIndex++] = (byte) (']' >> SerializerUtil.HI_BYTE_SHIFT);
		buf[writeIndex++] = (byte) (']' >> SerializerUtil.LO_BYTE_SHIFT);
	}
}