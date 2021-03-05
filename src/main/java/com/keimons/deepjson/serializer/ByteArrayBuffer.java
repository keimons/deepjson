package com.keimons.deepjson.serializer;

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

	@Override
	public String newString() {
		try {
			if (buf.length != strategy.writeIndex()) {
				System.err.println("length reset");
			}
			String str = (String) unsafe.allocateInstance(String.class);
			unsafe.putObject(str, SerializerUtil.VALUE_OFFSET_STRING, buf);
			unsafe.putByte(str, SerializerUtil.CODER_OFFSET_STRING, coder);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public final void writeMark(char mark) {
		ensureWritable(1);
		strategy.writeMark(mark);
	}

	@Override
	public void writeBoolean(boolean value) {
		ensureWritable(value ? 4 : 5);
		strategy.writeValue(value);
	}

	@Override
	public final void writeChar(char value) {
		ensureCoder((byte) (value >>> 8 == 0 ? 0 : 1));
		ensureWritable(3);
		strategy.writeValue(value);
	}

	@Override
	public final void writeInt(int value) {
		int writable = SerializerUtil.size(value);
		ensureWritable(writable);
		strategy.writeValue(writable, value);
	}

	@Override
	public void writeLong(long value) {
		int writable = SerializerUtil.size(value);
		ensureWritable(writable);
		strategy.writeValue(writable, value);
	}

	@Override
	public void writeFloat(float value) {
		String s = Float.toString(value);
		ensureWritable(s.length());
		strategy.writeValue(s);
	}

	@Override
	public void writeDouble(double value) {
		String s = Double.toString(value);
		ensureWritable(s.length());
		strategy.writeValue(s);
	}

	@ForceInline
	@Override
	public final void writeString(String value) {
		ensureCoder(unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING));
		int writable = SerializerUtil.length(value) + 2;
		ensureWritable(writable);
		strategy.writeValueWithMark(value);
	}

	@ForceInline
	@Override
	public void writeNull() {
		ensureWritable(4);
		strategy.writeNull();
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, boolean value) {
		int writable = 1 + fieldName.length() + (value ? 4 : 5);
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, char value) {
		// ensure coder
		ensureCoder((byte) (value >>> 8 == 0 ? 0 : 1));
		int writable = 4 + fieldName.length();
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, int value) {
		int length = SerializerUtil.size(value);
		int writable = 1 + fieldName.length() + length;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, long value) {
		int length = SerializerUtil.size(value);
		int writable = 1 + fieldName.length() + length;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, float value) {
		String s = Float.toString(value);
		int writable = 1 + fieldName.length() + s.length();
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, s);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, double value) {
		String s = Double.toString(value);
		int writable = 1 + fieldName.length() + s.length();
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, s);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, Object value) {
		int writable = 1 + fieldName.length();
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
		ISerializer serializer = SerializerFactory.getSerializer(value.getClass());
		serializer.write(value, this);
	}

	/**
	 * 写入对象结尾标识。
	 */
	@ForceInline
	@Override
	public void writeEndObject() {
		ensureWritable(1);
		strategy.writeEndObject();
	}

	/**
	 * 确保缓冲区的编码方式与即将写入的编码方式相同。
	 *
	 * @param coder 即将写入的编码方式
	 */
	private void ensureCoder(byte coder) {
		if (this.coder == 0 && coder == 1) {
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
		writableBytes <<= coder;
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
		strategy.setBuf(newBuf);
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