package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.filler.SerializerUtil;
import com.keimons.deepjson.filler.IFieldName;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

public class ByteBuf {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_L_BRACES = (byte) ('{' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACES = (byte) ('{' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACES = (byte) ('}' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACES = (byte) ('}' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_L_BRACKET = (byte) ('[' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACKET = (byte) ('[' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACKET = (byte) (']' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACKET = (byte) (']' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK1 = (byte) (',' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK1 = (byte) (',' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_NEGATIVE = (byte) ('-' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_NEGATIVE = (byte) ('-' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK = (byte) ('"' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK = (byte) ('"' << SerializerUtil.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_A = (byte) ('a' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_A = (byte) ('a' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_E = (byte) ('e' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_E = (byte) ('e' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_F = (byte) ('f' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_F = (byte) ('f' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_L = (byte) ('l' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L = (byte) ('l' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_N = (byte) ('n' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_N = (byte) ('n' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_R = (byte) ('r' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R = (byte) ('r' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_S = (byte) ('s' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_S = (byte) ('s' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_T = (byte) ('t' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_T = (byte) ('t' << SerializerUtil.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_U = (byte) ('u' << SerializerUtil.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_U = (byte) ('u' << SerializerUtil.LO_BYTE_SHIFT);


	private static final byte[] BOOLEAN_TRUE_UTF16 = {
			(byte) ('t' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('t' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('r' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('r' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('u' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('u' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('e' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('e' << SerializerUtil.LO_BYTE_SHIFT),
	};

	private static final byte[] BOOLEAN_FALSE_UTF16 = {
			(byte) ('f' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('f' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('a' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('a' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('l' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('l' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('s' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('s' << SerializerUtil.LO_BYTE_SHIFT),
			(byte) ('e' << SerializerUtil.HI_BYTE_SHIFT),
			(byte) ('e' << SerializerUtil.LO_BYTE_SHIFT),
	};

	private final long options;

	private IWriter<byte[]> writer;

	private byte[] buf;

	private byte coder = 1;

	private int writeIndex;

	boolean marked;

	private ByteBuf(long options, int capacity, byte coder) {
		this.buf = new byte[630];
		this.options = options;
		this.coder = coder;
		if (coder == SerializerUtil.LATIN) {
			writer = new WriterLatin();
		} else {
			writer = new WriterUtf16();
		}
	}

	public int writeBoolean(IFieldName field, boolean value) {
		int writable = field.length() + (value ? 4 : 5);
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeBoolean(buf, options, writeIndex, value);
		return writable;
	}

	public int writeChar(IFieldName field, char value) {
		int writable = field.length() + 3;
		ensureWritable(writable);
		ensureCoder((byte) (coder | (value >>> 8 == 0 ? SerializerUtil.LATIN : SerializerUtil.UTF16)));
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeChar(buf, options, writeIndex, value);
		return writable;
	}

	public void mark() {
		marked = true;
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, boolean value) {
		if (coder == 0) {
			buf[writeIndex++] = mark;
		} else {
			buf[writeIndex++] = (byte) (mark << SerializerUtil.HI_BYTE_SHIFT);
			buf[writeIndex++] = (byte) (mark << SerializerUtil.LO_BYTE_SHIFT);
			int length = fieldName.length;
			System.arraycopy(fieldName, 0, buf, writeIndex, length);
			writeIndex += length;
			if (value) {
				System.arraycopy(BOOLEAN_TRUE_UTF16, 0, buf, writeIndex, 8);
				writeIndex += 8;
			} else {
				System.arraycopy(BOOLEAN_FALSE_UTF16, 0, buf, writeIndex, 10);
				writeIndex += 10;
			}
		}
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, int value) {
		if (coder == 0) {
			buf[writeIndex++] = mark;
		} else {
			buf[writeIndex++] = (byte) (mark << SerializerUtil.HI_BYTE_SHIFT);
			buf[writeIndex++] = (byte) (mark << SerializerUtil.LO_BYTE_SHIFT);
			int length = fieldName.length;
			System.arraycopy(fieldName, 0, buf, writeIndex, length);
			writeIndex += length;
			length = SerializerUtil.size(value) << 1;
			this.writeIndex += length;
			int q, r;
			int charPos = writeIndex;

			boolean negative = (value < 0);
			if (!negative) {
				value = -value;
			}

			// Get 2 digits/iteration using ints
			while (value <= -100) {
				q = value / 100;
				r = (q * 100) - value;
				value = q;
				buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
				buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
				buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
				buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
			}

			// We know there are at most two digits left at this point.
			q = value / 10;
			r = (q * 10) - value;
			buf[--charPos] = (byte) ('0' + r >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) ('0' + r >> SerializerUtil.HI_BYTE_SHIFT);

			// Whatever left is the remaining digit.
			if (q < 0) {
				buf[--charPos] = (byte) ('0' - q >> SerializerUtil.LO_BYTE_SHIFT);
				buf[--charPos] = (byte) ('0' - q >> SerializerUtil.HI_BYTE_SHIFT);
			}

			if (negative) {
				buf[--charPos] = HI_BYTE_NEGATIVE;
				buf[--charPos] = HI_BYTE_NEGATIVE;
			}
		}
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, long value) {

	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, float value) {

	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, double value) {

	}

	@ForceInline
	public void writeBytes(byte[] values, int value) {
		int length = values.length;
		System.arraycopy(values, 0, buf, writeIndex, length);
		writeIndex += length;
		length = SerializerUtil.size(value) << 1;
		this.writeIndex += length;
		int q, r;
		int charPos = writeIndex;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using ints
		while (value <= -100) {
			q = value / 100;
			r = (q * 100) - value;
			value = q;
			buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		buf[--charPos] = (byte) ('0' + r >> SerializerUtil.LO_BYTE_SHIFT);
		buf[--charPos] = (byte) ('0' + r >> SerializerUtil.HI_BYTE_SHIFT);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--charPos] = (byte) ('0' - q >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) ('0' - q >> SerializerUtil.HI_BYTE_SHIFT);
		}

		if (negative) {
			buf[--charPos] = HI_BYTE_NEGATIVE;
			buf[--charPos] = HI_BYTE_NEGATIVE;
		}
		buf[writeIndex++] = HI_BYTE_MARK1;
		buf[writeIndex++] = LO_BYTE_MARK1;
	}

	@ForceInline
	public void writeInt(int value) {
		int length = SerializerUtil.size(value) << 1;
		this.writeIndex += length;
		int q, r;
		int charPos = writeIndex;

		boolean negative = (value < 0);
		if (!negative) {
			value = -value;
		}

		// Get 2 digits/iteration using ints
		while (value <= -100) {
			q = value / 100;
			r = (q * 100) - value;
			value = q;
			buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitOnes[r] >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) (SerializerUtil.DigitTens[r] >> SerializerUtil.HI_BYTE_SHIFT);
		}

		// We know there are at most two digits left at this point.
		q = value / 10;
		r = (q * 10) - value;
		buf[--charPos] = (byte) ('0' + r >> SerializerUtil.LO_BYTE_SHIFT);
		buf[--charPos] = (byte) ('0' + r >> SerializerUtil.HI_BYTE_SHIFT);

		// Whatever left is the remaining digit.
		if (q < 0) {
			buf[--charPos] = (byte) ('0' - q >> SerializerUtil.LO_BYTE_SHIFT);
			buf[--charPos] = (byte) ('0' - q >> SerializerUtil.HI_BYTE_SHIFT);
		}

		if (negative) {
			buf[--charPos] = (byte) ('-' >> SerializerUtil.HI_BYTE_SHIFT);
			buf[--charPos] = (byte) ('-' >> SerializerUtil.LO_BYTE_SHIFT);
		}
		buf[writeIndex++] = HI_BYTE_MARK1;
		buf[writeIndex++] = LO_BYTE_MARK1;
	}

	public int writeInt(IFieldName field, int value) {
		int length = SerializerUtil.size(value);
		int writable = field.length() + length;
		ensureWritable(writable);
		int writeIndex = this.writeIndex << 1;
		byte[] bytes = field.getFieldNameByUtf16();
		System.arraycopy(bytes, 0, buf, writeIndex, bytes.length);
//		for (byte b : bytes) {
//			buf[writeIndex++] = b;
//		}
		this.writeIndex += bytes.length >> 1;
		this.writeIndex += length;
		SerializerUtil.putUTF16(buf, this.writeIndex, value);
		writeIndex = this.writeIndex << 1;
		buf[writeIndex++] = HI_BYTE_MARK1;
		buf[writeIndex] = LO_BYTE_MARK1;
		this.writeIndex++;
//		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
//		this.writeIndex += length;
//		this.writeIndex += writer.writeInt(buf, options, writeIndex, value);
		return writable;
	}

	public int writeLong(IFieldName field, long value) {
		int length = SerializerUtil.size(value);
		int writable = field.length() + length;
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += length;
		this.writeIndex += writer.writeLong(buf, options, writeIndex, value);
		return writable;
	}

	public int writeStringWithNoMark(IFieldName field, String value) {
		int writable = field.length() + value.length();
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeString(buf, options, writeIndex, value);
		return writable;
	}

	public int writeDouble(IFieldName field, double value) {
		String s = String.valueOf(value);
		int writable = field.length() + s.length();
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeString(buf, options, writeIndex, s);
		return writable;
	}

	public int writeString(IFieldName field, String value) {
		int writable = field.length() + value.length() + 2;
		ensureWritable(writable);
		ensureCoder(unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING));
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeStringWithMark(buf, options, writeIndex, value);
		return writable;
	}

	public int writeObject(IFieldName field, Object value) {
		int writable = field.length();
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);

		ISerializer writer = SerializerFactory.getWriter(value.getClass());
		int write = writer.write(value, this);

		this.writeIndex += this.writer.writeMark(buf, options, writeIndex);
		return writable + write;
	}

	public int writeInts(int[] value) {
		return writer.writeInts(buf, options, writeIndex, value);
	}

	public void writeStartObject() {
		ensureWritable(2);
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex++] = LO_BYTE_L_BRACES;
	}

	/**
	 * 写入对象结尾标识。
	 */
	public void writeEndObject() {
		ensureWritable(2);
		buf[writeIndex++] = HI_BYTE_R_BRACES;
		buf[writeIndex++] = LO_BYTE_R_BRACES;
	}

	public int writeStartArray() {
		ensureWritable(1);
		this.writeIndex += writer.writeStartArray(buf, options, writeIndex);
		return 1;
	}

	/**
	 * 写入数组结尾标识。
	 *
	 * @param override 是否重写最后一个字符
	 * @return 写入字符数量
	 */
	public int writeEndArray(boolean override) {
		if (override) {
			writeIndex--;
		} else {
			ensureWritable(1);
		}
		this.writeIndex += writer.writeEndArray(buf, options, writeIndex);
		return override ? 0 : 1;
	}

//	public int writeTrue() {
//		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
//		if (coder == FillerHelper.LATIN) {
//			buf[++writeIndex] = 't';
//			buf[++writeIndex] = 'r';
//			buf[++writeIndex] = 'u';
//			buf[++writeIndex] = 'e';
//		} else {
//			buf[++writeIndex] = HI_BYTE_T;
//			buf[++writeIndex] = LO_BYTE_T;
//			buf[++writeIndex] = HI_BYTE_R;
//			buf[++writeIndex] = LO_BYTE_R;
//			buf[++writeIndex] = HI_BYTE_U;
//			buf[++writeIndex] = LO_BYTE_U;
//			buf[++writeIndex] = HI_BYTE_E;
//			buf[++writeIndex] = LO_BYTE_E;
//		}
//		this.writeIndex += 4;
//		return 4;
//	}
//
//	public int writeFalse() {
//		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
//		if (coder == FillerHelper.LATIN) {
//			buf[++writeIndex] = 'f';
//			buf[++writeIndex] = 'a';
//			buf[++writeIndex] = 'l';
//			buf[++writeIndex] = 's';
//			buf[++writeIndex] = 'e';
//		} else {
//			buf[++writeIndex] = HI_BYTE_F;
//			buf[++writeIndex] = LO_BYTE_F;
//			buf[++writeIndex] = HI_BYTE_A;
//			buf[++writeIndex] = LO_BYTE_A;
//			buf[++writeIndex] = HI_BYTE_L;
//			buf[++writeIndex] = LO_BYTE_L;
//			buf[++writeIndex] = HI_BYTE_S;
//			buf[++writeIndex] = LO_BYTE_S;
//			buf[++writeIndex] = HI_BYTE_E;
//			buf[++writeIndex] = LO_BYTE_E;
//		}
//		this.writeIndex += 5;
//		return 5;
//	}

	public int writeNull() {
		if (SerializerOptions.IgnoreNonField.isOptions(options)) {
			ensureWritable(5);
			this.writeIndex += writer.writeNull(buf, options, writeIndex);
			return 5;
		} else {
			return 0;
		}
	}

	/**
	 * 确保缓冲区的编码方式与即将写入的编码方式相同。
	 *
	 * @param coder 即将写入的编码方式
	 */
	public void ensureCoder(byte coder) {
		if (this.coder != coder) {
			throw new CoderModificationException();
		}
	}

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writableBytes 即将写入的字节数
	 */
	public void ensureWritable(int writableBytes) {
		if (writableBytes + writeIndex > buf.length) {
			if (true) {
				expandCapacity(writableBytes + writeIndex);
			} else {
				throw new CapacityModificationException();
			}
		}
	}

	/**
	 * 扩容
	 *
	 * @param minCapacity 最小容量
	 */
	private void expandCapacity(int minCapacity) {
		int newCapacity = (buf.length >> 1);

		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		byte[] newBuf = new byte[newCapacity];
		System.arraycopy(buf, 0, newBuf, 0, writeIndex);
		buf = newBuf;
	}

	public long getOptions() {
		return options;
	}

	public IWriter<byte[]> getWriter() {
		return writer;
	}

	public void setWriter(IWriter<byte[]> writer) {
		this.writer = writer;
	}

	public byte[] getBuf() {
		return buf;
	}

	public void setBuf(byte[] buf) {
		this.buf = buf;
	}

	public byte getCoder() {
		return coder;
	}

	public void setCoder(byte coder) {
		this.coder = coder;
	}

	public int getWriteIndex() {
		return writeIndex;
	}

	public void setWriteIndex(int writeIndex) {
		this.writeIndex = writeIndex;
	}

	public String newString() {
		try {
			String str = (String) unsafe.allocateInstance(String.class);
			unsafe.putObject(str, SerializerUtil.VALUE_OFFSET_STRING, buf);
			unsafe.putByte(str, SerializerUtil.CODER_OFFSET_STRING, coder);
			return str;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ByteBuf buffer(long options, int initCapacity, byte coder) {
		return new ByteBuf(options, initCapacity, coder);
	}
}