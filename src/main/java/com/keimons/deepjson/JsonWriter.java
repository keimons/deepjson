package com.keimons.deepjson;

import com.keimons.deepjson.support.DefaultWriter;
import com.keimons.deepjson.util.CodecUtil;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;

/**
 * 缓冲区
 * <p>
 * 在jdk 1.6-1.8中，使用char数组类型缓冲区。在jdk 9+中使用byte数组类型缓冲区。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class JsonWriter implements Closeable {

	public static final char[] CHAR_HEX = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};

	public static final char[][] REPLACEMENT_CHARS;

	static {
		REPLACEMENT_CHARS = new char[256][];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = new char[]{
					'\\', 'u', '0', '0', CodecUtil.CHAR_HEX[i >> 4 & 0xF], CodecUtil.CHAR_HEX[i & 0xF]
			};
		}
		REPLACEMENT_CHARS['"'] = new char[]{'\\', '\"'};
		REPLACEMENT_CHARS['\\'] = new char[]{'\\', '\\'};
		REPLACEMENT_CHARS['\t'] = new char[]{'\\', 't'};
		REPLACEMENT_CHARS['\b'] = new char[]{'\\', 'b'};
		REPLACEMENT_CHARS['\n'] = new char[]{'\\', 'n'};
		REPLACEMENT_CHARS['\r'] = new char[]{'\\', 'r'};
		REPLACEMENT_CHARS['\f'] = new char[]{'\\', 'f'};

		REPLACEMENT_CHARS[127] = new char[]{'\\', 'u', '0', '0', '7', 'f'};

		for (int i = 129; i <= 159; i++) {
			REPLACEMENT_CHARS[i] = new char[]{
					'\\', 'u', '0', '0', CodecUtil.CHAR_HEX[i >> 4 & 0xF], CodecUtil.CHAR_HEX[i & 0xF]
			};
		}
	}

	/**
	 * 编码选项
	 */
	protected long options;

	protected Buffer buf;

	/**
	 * 分配一个复合缓冲区
	 *
	 * @return 复合缓冲区
	 */
	public static JsonWriter defaultWriter() {
		return new DefaultWriter();
	}

//	/**
//	 * 写入指定位置
//	 *
//	 * @param <T>       返回值类型
//	 * @param generator 写入策略
//	 * @param dest      写入目标
//	 * @param offset    偏移位置
//	 * @return 返回内容
//	 * @throws WriteFailedException 写入失败异常
//	 */
//	public <T> T writeTo(Generator<T> generator, T dest, int offset) throws WriteFailedException {
//		int length = bufferIndex << CodecConfig.HIGHEST + writeIndex;
//		return generator.generate(dest, offset, buffers, length, bufferIndex, writeIndex);
//	}

	/**
	 * 写入一个分割标识
	 *
	 * @param mark 标识值
	 */
	public abstract void writeMark(char mark) throws IOException;

	/**
	 * 写入boolean值
	 *
	 * @param value boolean值
	 */
	public abstract void write(boolean value) throws IOException;

	/**
	 * 写入int值
	 *
	 * @param value int值
	 */
	public abstract void write(int value) throws IOException;

	/**
	 * 写入long值
	 *
	 * @param value long值
	 */
	public abstract void write(long value) throws IOException;

	/**
	 * 写入float值
	 *
	 * @param value float值
	 */
	public abstract void write(float value) throws IOException;

	/**
	 * 写入double值
	 *
	 * @param value double值
	 */
	public abstract void write(double value) throws IOException;

	/**
	 * 写入{@link String}值
	 *
	 * @param value {@link String}值
	 */
	public abstract void write(String value) throws IOException;

	/**
	 * 写入char值和双引号
	 *
	 * @param value char值
	 */
	public abstract void writeWithQuote(char value) throws IOException;

	/**
	 * 写入{@link String}值
	 *
	 * @param value {@link String}值
	 */
	public final void writeWithQuote(String value) throws IOException {
		write(value);
	}

	/**
	 * 写入字段
	 *
	 * @param mark 前置标识
	 * @param name 字段信息
	 */
	public abstract void writeName(char mark, char[] name) throws IOException;

	/**
	 * 写入boolean字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value boolean值
	 */
	public final void writeValue(char mark, char[] name, boolean value) throws IOException {
		writeName(mark, name);
		write(value);
	}

	/**
	 * 写入char字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value char值
	 */
	public final void writeValue(char mark, char[] name, char value) throws IOException {
		writeName(mark, name);
		writeWithQuote(value);
	}

	/**
	 * 写入int字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value int值
	 */
	public final void writeValue(char mark, char[] name, int value) throws IOException {
		writeName(mark, name);
		write(value);
	}

	/**
	 * 写入long字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value long值
	 */
	public final void writeValue(char mark, char[] name, long value) throws IOException {
		writeName(mark, name);
		write(value);
	}

	/**
	 * 写入float字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value long值
	 */
	public final void writeValue(char mark, char[] name, float value) throws IOException {
		writeName(mark, name);
		write(value);
	}

	/**
	 * 写入double字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value double值
	 */
	public final void writeValue(char mark, char[] name, double value) throws IOException {
		writeName(mark, name);
		write(value);
	}

	/**
	 * 写入{@link String}字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value {@link String}值
	 */
	public final void writeValue(char mark, char[] name, @Nullable String value) throws IOException {
		writeName(mark, name);
		if (value == null) {
			writeNull();
		} else {
			write(value);
		}
	}

	/**
	 * 写入{@code null}字符串
	 */
	public abstract void writeNull() throws IOException;

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writable 即将写入的字节数
	 * @return 是否横跨缓冲区。{@code true}横跨缓冲区写入，{@code false}当前缓冲区写入。
	 */
	public boolean ensureWritable(int writable) {
		return buf.ensureWritable(writable);
	}

	/**
	 * 初始化
	 *
	 * @param buf     缓冲区
	 * @param options 编码选项
	 */
	public void init(Buffer buf, long options) {
		this.buf = buf;
		this.options = options;
	}

	/**
	 * 释放缓冲区
	 */
	public abstract void close() throws IOException;
}