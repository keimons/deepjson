package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

public abstract class ByteBuf {

	protected static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	protected final long options;

	protected IWriterStrategy strategy;

	protected ByteBuf(long options) {
		this.options = options;
	}

	public static ByteBuf buffer(long options, int initCapacity, byte coder) {
		int version = PlatformUtil.javaVersion();
		if (version >= 9) {
			return new ByteArrayBuffer(options, initCapacity, coder);
		} else {
			return null;
		}
	}

	public abstract String newString();

	public abstract void writeValue(byte mark, IFieldName fieldName, boolean value);

	public abstract void writeValue(byte mark, IFieldName fieldName, char value);

	public abstract void writeValue(byte mark, IFieldName fieldName, int value);

	public abstract void writeValue(byte mark, IFieldName fieldName, long value);

	public abstract void writeValue(byte mark, IFieldName fieldName, float value);

	public abstract void writeValue(byte mark, IFieldName fieldName, double value);

	public abstract void writeValue(byte mark, IFieldName fieldName, Object value);

	public abstract void writeString(String value);

	/**
	 * 写入对象结尾标识。
	 */
	@ForceInline
	public abstract void writeEndObject();

	public abstract int writeNull();

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writableBytes 即将写入的字节数
	 */
	public abstract void ensureWritable(int writableBytes);

	/**
	 * 扩容
	 *
	 * @param minCapacity 最小容量
	 */
	protected abstract void expandCapacity(int minCapacity);

	public long getOptions() {
		return options;
	}

	public IWriterStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(IWriterStrategy strategy) {
		this.strategy = strategy;
	}
}