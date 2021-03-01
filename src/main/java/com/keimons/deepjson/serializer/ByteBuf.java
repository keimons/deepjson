package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

public class ByteBuf {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final long options;

	private IWriter<byte[]> writer;

	private byte[] buf;

	private byte coder = 1;

	private int writeIndex;

	private int markLength;

	private ByteBuf(long options, int capacity, byte coder) {
		this.buf = new byte[capacity << coder];
		this.options = options;
		this.coder = coder;
		if (coder == SerializerUtil.LATIN) {
			markLength = 1;
			writer = new LatinWriter(options, buf, writeIndex);
		} else {
			markLength = 2;
			writer = new Utf16Writer(options, buf, writeIndex);
		}
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, boolean value) {
		int writable = markLength + fieldName.length + (value ? 4 : 5) << coder;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, value);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, char value) {
		int writable = markLength + fieldName.length + 3 << coder;
		ensureWritable(writable);
		// ensure mix coder
		ensureCoder((byte) (value >>> 8 == 0 ? 0 : 1));
		writer.writeValue(mark, fieldName, value);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, int value) {
		int length = SerializerUtil.size(value) << coder;
		int writable = markLength + fieldName.length + length;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, long value) {
		int length = SerializerUtil.size(value) << coder;
		int writable = markLength + fieldName.length + length;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, float value) {
		String s = Float.toString(value);
		int writable = markLength + fieldName.length + s.length() << coder;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, s);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, double value) {
		String s = Double.toString(value);
		int writable = markLength + fieldName.length + s.length() << coder;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, s);
	}

	@ForceInline
	public void writeValue(byte mark, byte[] fieldName, Object value) {
		int writable = markLength + fieldName.length;
		ensureWritable(writable);
		writer.writeValue(mark, fieldName, value);
		ISerializer serializer = SerializerFactory.getSerializer(value.getClass());
		serializer.write(value, this);
	}

	public void writeString(String value) {
		int writable = value.length() + 2;
		ensureWritable(writable);
		byte coder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
		ensureCoder(coder);
//		this.writeIndex += writer.writeStringWithMark(buf, options, writeIndex, value);
	}

	/**
	 * 写入对象结尾标识。
	 */
	@ForceInline
	public void writeEndObject() {
		ensureWritable(1 << coder);
		writer.writeEndObject();
	}

	public int writeNull() {
		if (SerializerOptions.IgnoreNonField.isOptions(options)) {
			ensureWritable(5);
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ByteBuf buffer(long options, int initCapacity, byte coder) {
		return new ByteBuf(options, initCapacity, coder);
	}
}