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

	/**
	 * 根据缓冲区生成字符串
	 *
	 * @return 字符串
	 */
	public abstract String newString();

	/**
	 * 写入一个分割标识
	 *
	 * @param mark 标识值
	 */
	public abstract void writeMark(char mark);

	/**
	 * 写入boolean值
	 *
	 * @param value boolean值
	 */
	public abstract void writeBoolean(boolean value);

	/**
	 * 写入char值
	 *
	 * @param value char值
	 */
	public abstract void writeChar(char value);

	/**
	 * 写入int值
	 *
	 * @param value int值
	 */
	public abstract void writeInt(int value);

	/**
	 * 写入long值
	 *
	 * @param value long值
	 */
	public abstract void writeLong(long value);

	/**
	 * 写入float值
	 *
	 * @param value float值
	 */
	public abstract void writeFloat(float value);

	/**
	 * 写入double值
	 *
	 * @param value double值
	 */
	public abstract void writeDouble(double value);

	/**
	 * 写入String值
	 *
	 * @param value String值
	 */
	public abstract void writeString(String value);

	/**
	 * 写入boolean字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     boolean值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, boolean value);

	/**
	 * 写入char字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     char值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, char value);

	/**
	 * 写入int字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     int值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, int value);

	/**
	 * 写入long字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     long值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, long value);

	/**
	 * 写入float字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     float值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, float value);

	/**
	 * 写入double字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     double值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, double value);

	/**
	 * 写入Object字段
	 *
	 * @param mark      前置标识
	 * @param fieldName 字段信息
	 * @param value     object值
	 */
	public abstract void writeValue(byte mark, IFieldName fieldName, Object value);

	/**
	 * 写入对象结尾标识。
	 */
	public abstract void writeEndObject();

	/**
	 * 写入{@code null}字符串
	 */
	public abstract void writeNull();

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writable 即将写入的字节数
	 */
	public abstract void ensureWritable(int writable);

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