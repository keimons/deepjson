package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFieldName;
import sun.misc.Unsafe;

public class ByteBuf {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_MARK1 = (byte) (',' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK1 = (byte) (',' << FillerHelper.LO_BYTE_SHIFT);

	private final long options;

	private IWriter<byte[]> writer;

	private byte[] buf;

	private byte coder;

	private int writeIndex;

	private ByteBuf(long options, int capacity, byte coder) {
		this.buf = new byte[capacity << coder];
		this.options = options;
		this.coder = coder;
		if (coder == FillerHelper.LATIN) {
			writer = new WriterLatin();
		} else {
			writer = new WriterUtf16();
		}
	}

	public int writeBoolean(IFieldName field, boolean value) {
		int writable = field.size() + (value ? 4 : 5);
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeBoolean(buf, options, writeIndex, value);
		return writable;
	}

	public int writeChar(IFieldName field, char value) {
		int writable = field.size() + 3;
		ensureWritable(writable);
		ensureCoder((byte) (coder | (value >>> 8 == 0 ? FillerHelper.LATIN : FillerHelper.UTF16)));
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeChar(buf, options, writeIndex, value);
		return writable;
	}

	public int writeInt(IFieldName field, int value) {
		int length = FillerHelper.size(value);
		int writable = field.size() + length;
		ensureWritable(writable);
		if (coder == 0) {
			for (byte b : field.getFieldNameByLatin()) {
				buf[writeIndex++] = b;
			}
			this.writeIndex += length;
			FillerHelper.putLATIN(buf, writeIndex, value);
			buf[writeIndex++] = ',';
		} else {
			int writeIndex = this.writeIndex << 1;
			byte[] bytes = field.getFieldNameByLatin();
			for (byte b : bytes) {
				buf[writeIndex++] = b;
			}
			this.writeIndex += bytes.length >> 1;
			this.writeIndex += length;
			FillerHelper.putUTF16(buf, this.writeIndex, value);
			writeIndex <<= 1;
			buf[writeIndex++] = HI_BYTE_MARK1;
			buf[writeIndex] = LO_BYTE_MARK1;
			this.writeIndex++;
		}
//		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
//		this.writeIndex += length;
//		this.writeIndex += writer.writeInt(buf, options, writeIndex, value);
		return writable;
	}

	public int writeLong(IFieldName field, long value) {
		int length = FillerHelper.size(value);
		int writable = field.size() + length;
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += length;
		this.writeIndex += writer.writeLong(buf, options, writeIndex, value);
		return writable;
	}

	public int writeStringWithNoMark(IFieldName field, String value) {
		int writable = field.size() + value.length();
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeString(buf, options, writeIndex, value);
		return writable;
	}

	public int writeDouble(IFieldName field, double value) {
		String s = String.valueOf(value);
		int writable = field.size() + s.length();
		ensureWritable(writable);
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeString(buf, options, writeIndex, s);
		return writable;
	}

	public int writeString(IFieldName field, String value) {
		int writable = field.size() + value.length() + 2;
		ensureWritable(writable);
		ensureCoder(unsafe.getByte(value, FillerHelper.CODER_OFFSET_STRING));
		this.writeIndex += writer.writeField(buf, options, writeIndex, field);
		this.writeIndex += writer.writeStringWithMark(buf, options, writeIndex, value);
		return writable;
	}

	public int writeObject(IFieldName field, Object value) {
		int writable = field.size();
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

	public int writeStartObject() {
		ensureWritable(1);
		this.writeIndex += writer.writeStartObject(buf, options, writeIndex);
		return 1;
	}

	/**
	 * 写入对象结尾标识。
	 *
	 * @param override 是否重写最后一个字符
	 * @return 写入字符数量
	 */
	public int writeEndObject(boolean override) {
		if (override) {
			writeIndex--;
		} else {
			ensureWritable(1);
		}
		this.writeIndex += writer.writeEndObject(buf, options, writeIndex);
		return override ? 0 : 1;
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
			unsafe.putObject(str, FillerHelper.VALUE_OFFSET_STRING, buf);
			unsafe.putByte(str, FillerHelper.CODER_OFFSET_STRING, coder);
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