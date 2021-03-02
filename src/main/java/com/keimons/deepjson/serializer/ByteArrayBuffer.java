package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * 字节数组缓冲区
 *
 * @author monkey
 * @version 1.0
 * @since 9
 **/
class ByteArrayBuffer extends ByteBuf {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private byte[] buf;

	private byte coder;

	ByteArrayBuffer(long options, int capacity, byte coder) {
		super(options);
		this.coder = coder;
		this.buf = new byte[capacity << coder];
		if (coder == SerializerUtil.LATIN) {
			strategy = new LatinWriterPolicy(options, buf, 0);
		} else {
			strategy = new Utf16WriterPolicy(options, buf, 0);
		}
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

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, boolean value) {
		int writable = (1 + fieldName.length() + (value ? 4 : 5)) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, char value) {
		// ensure coder
		ensureCoder((byte) (value >>> 8 == 0 ? 0 : 1));
		int writable = (1 + fieldName.length() + 3) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, int value) {
		int length = SerializerUtil.size(value);
		int writable = (1 + fieldName.length() + length) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, long value) {
		int length = SerializerUtil.size(value);
		int writable = (1 + fieldName.length() + length) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, float value) {
		String s = Float.toString(value);
		int writable = (1 + fieldName.length() + s.length()) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, s);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, double value) {
		String s = Double.toString(value);
		int writable = (1 + fieldName.length() + s.length()) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, s);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, Object value) {
		int writable = (1 + fieldName.length()) << coder;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
		ISerializer serializer = SerializerFactory.getSerializer(value.getClass());
		serializer.write(value, this);
	}

	@ForceInline
	@Override
	public final void writeString(String value) {
		ensureCoder(coder);
		int writable = value.length() + 2;
		ensureWritable(writable);
		byte coder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
//		this.writeIndex += writer.writeStringWithMark(buf, options, writeIndex, value);
	}

	/**
	 * 写入对象结尾标识。
	 */
	@ForceInline
	@Override
	public void writeEndObject() {
		ensureWritable(1 << coder);
		strategy.writeEndObject();
	}

	@ForceInline
	@Override
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
	private void ensureCoder(byte coder) {
		if (this.coder != coder) {
			throw new CoderModificationException();
		}
	}

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writableBytes 即将写入的字节数
	 */
	@Override
	public void ensureWritable(int writableBytes) {
		// System.out.println("writeIndex: " + strategy.writeIndex() + ", writableBytes: " + writableBytes + ", capacity: " + buf.length);
		if (writableBytes + strategy.writeIndex() > buf.length) {
			if (true) {
				expandCapacity(writableBytes + strategy.writeIndex());
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
	@Override
	protected void expandCapacity(int minCapacity) {
		System.err.println("expand capacity");

		int newCapacity = (buf.length >> 1);

		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		byte[] newBuf = new byte[newCapacity];
		System.arraycopy(buf, 0, newBuf, 0, strategy.writeIndex());
		buf = newBuf;
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
}