package com.keimons.deepjson.buffer;

import com.keimons.deepjson.field.IFieldName;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import jdk.internal.vm.annotation.ForceInline;
import sun.misc.Unsafe;

/**
 * 缓冲区
 * <p>
 * 在jdk 1.7-1.8中，使用char数组类型缓冲区。在jdk 9+中使用byte数组类型缓冲区。
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public abstract class ByteBuf {

	protected static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	/**
	 * 序列化选项
	 */
	protected final long options;

	/**
	 * 写入策略
	 */
	protected IWriterStrategy strategy;

	/**
	 * 创建缓冲区
	 *
	 * @param options 序列化选项
	 */
	protected ByteBuf(long options) {
		this.options = options;
	}

	/**
	 * 分配一个缓冲区
	 *
	 * @param options      序列化选项
	 * @param initCapacity 缓冲区长度
	 * @param coder        编码方式
	 * @return 缓冲区
	 */
	public static ByteBuf buffer(long options, int initCapacity, byte coder) {
		int version = PlatformUtil.javaVersion();
		if (version >= 9) {
			return new ByteArrayBuffer(options, initCapacity, coder);
		} else {
			return new CharArrayBuffer(options, initCapacity);
		}
	}

	public abstract String newString();

	public abstract void writeMark(char mark);

	public abstract void writeBoolean(boolean value);

	public abstract void writeChar(char value);

	public abstract void writeInt(int value);

	public abstract void writeLong(long value);

	public abstract void writeFloat(float value);

	public abstract void writeDouble(double value);

	public abstract void writeString(String value);

	public abstract void writeValue(byte mark, IFieldName fieldName, boolean value);

	public abstract void writeValue(byte mark, IFieldName fieldName, char value);

	public abstract void writeValue(byte mark, IFieldName fieldName, int value);

	public abstract void writeValue(byte mark, IFieldName fieldName, long value);

	public abstract void writeValue(byte mark, IFieldName fieldName, float value);

	public abstract void writeValue(byte mark, IFieldName fieldName, double value);

	public abstract void writeValue(byte mark, IFieldName fieldName, Object value);

	/**
	 * 写入对象结尾标识。
	 */
	@ForceInline
	public abstract void writeEndObject();

	/**
	 * 写入一个空
	 */
	public abstract void writeNull();

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