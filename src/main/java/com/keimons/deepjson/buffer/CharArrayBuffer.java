package com.keimons.deepjson.buffer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.serializer.CapacityModificationException;
import com.keimons.deepjson.field.IFieldName;
import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.SerializerFactory;
import com.keimons.deepjson.util.SerializerUtil;
import jdk.internal.vm.annotation.ForceInline;

/**
 * char数组缓冲区
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
class CharArrayBuffer extends ByteBuf {

	private char[] buf;

	private int writeIndex;

	CharArrayBuffer(long options, int capacity) {
		super(options);
		this.buf = new char[capacity];
	}

	@Override
	public String newString() {
		try {
			return new String(buf);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void writeMark(char mark) {
		ensureWritable(1);
		strategy.writeValueWithQuote(mark);
	}

	@Override
	public void writeBoolean(boolean value) {
		ensureWritable(value ? 4 : 5);
		strategy.writeValue(value);
	}

	@Override
	public void writeChar(char value) {
		ensureWritable(3);
		strategy.writeValueWithQuote(value);
	}

	@Override
	public void writeInt(int value) {
		int writable = SerializerUtil.length(value);
		ensureWritable(writable);
		strategy.writeValue(writable, value);
	}

	@Override
	public void writeLong(long value) {
		int writable = SerializerUtil.length(value);
		ensureWritable(writable);
		strategy.writeValue(writable, value);
	}

	@Override
	public void writeFloat(float value) {
		String s = Float.toString(value);
		ensureWritable(s.length());
		strategy.writeValueWithQuote(s);
	}

	@Override
	public void writeDouble(double value) {
		String s = Double.toString(value);
		ensureWritable(s.length());
		strategy.writeValueWithQuote(s);
	}

	@ForceInline
	@Override
	public final void writeString(String value) {
		int writable = value.length() + 2;
		ensureWritable(writable);
		byte coder = unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING);
//		this.writeIndex += writer.writeStringWithMark(buf, options, writeIndex, value);
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
		int writable = 1 + fieldName.length() + 3;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, int value) {
		int length = SerializerUtil.length(value);
		int writable = 1 + fieldName.length() + length;
		ensureWritable(writable);
		strategy.writeValue(mark, fieldName, length, value);
	}

	@ForceInline
	@Override
	public final void writeValue(byte mark, IFieldName fieldName, long value) {
		int length = SerializerUtil.length(value);
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

	@ForceInline
	@Override
	public void writeNull() {
		if (SerializerOptions.IgnoreNonField.isOptions(options)) {
			ensureWritable(5);
		} else {
		}
	}

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writable 即将写入的字节数
	 */
	@Override
	public void ensureWritable(int writable) {
		// System.out.println("writeIndex: " + strategy.writeIndex() + ", writableBytes: " + writableBytes + ", capacity: " + buf.length);
		if (!strategy.ensureWritable(writable)) {
			if (true) {
				expandCapacity(writable + strategy.length());
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

		int newCapacity = (buf.length << 1);

		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		char[] newBuf = new char[newCapacity];
		System.arraycopy(buf, 0, newBuf, 0, buf.length);
		buf = newBuf;
	}
}