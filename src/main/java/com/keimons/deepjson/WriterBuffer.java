package com.keimons.deepjson;

import com.keimons.deepjson.support.buffer.CompositeBuffer;
import com.keimons.deepjson.support.buffer.SafeBuffer;
import com.keimons.deepjson.util.CodecUtil;
import com.keimons.deepjson.util.WriteFailedException;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;

/**
 * 缓冲区
 * <p>
 * 在jdk 1.6-1.8中，使用char数组类型缓冲区。在jdk 9+中使用byte数组类型缓冲区。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class WriterBuffer implements Closeable {

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

	protected final StringBuilder DECIMAL = new StringBuilder(64);

	/**
	 * 复合缓冲区
	 */
	protected char[][] buffers;

	/**
	 * 写入缓冲区
	 */
	protected int bufferIndex;

	/**
	 * 当前正在写入的缓冲区
	 */
	protected char[] buf;

	Appendable appendable;

	/**
	 * 写入位置
	 */
	protected int writeIndex;

	/**
	 * 缓冲区容量
	 */
	protected int capacity;

	protected WriterBuffer(int size) {
		buffers = new char[size][];
	}

	/**
	 * 分配一个复合缓冲区
	 *
	 * @return 复合缓冲区
	 */
	public static WriterBuffer compositeBuffer() {
		return new CompositeBuffer();
	}

	/**
	 * 分配一个缓冲区
	 *
	 * @return 缓冲区
	 */
	public static WriterBuffer buffer() {
		return new SafeBuffer();
	}

	/**
	 * 写入char值
	 *
	 * @param value char
	 */
	protected void write(char value) {
		buf[writeIndex++] = value;
	}

	/**
	 * 按照unicode的编码方式写入一个char，所有字符均转码。
	 *
	 * @param value char
	 */
	protected void writeUnicode(char value) {
		buf[writeIndex++] = '\\';
		buf[writeIndex++] = 'u';
		buf[writeIndex++] = CHAR_HEX[value >> 12 & 0xF];
		buf[writeIndex++] = CHAR_HEX[value >> 8 & 0xF];
		buf[writeIndex++] = CHAR_HEX[value >> 4 & 0xF];
		buf[writeIndex++] = CHAR_HEX[value & 0xF];
	}

	/**
	 * 使用常规方式写入一个char，仅对特殊字符进行转码。
	 *
	 * @param value char
	 */
	protected void writeNormal(char value) {
		if (value < 256) {
			char[] chars = REPLACEMENT_CHARS[value];
			if (chars == null) {
				buf[writeIndex++] = value;
			} else {
				for (char replace : chars) {
					buf[writeIndex++] = replace;
				}
			}
		} else {
			if (value == 0x2028 || value == 0x2029) { // 0x2028 0x2029
				writeUnicode(value);
			} else {
				buf[writeIndex++] = value;
			}
		}
	}

	private void doWrite() {
		int writable = DECIMAL.length();
		ensureWritable(writable);
		DECIMAL.getChars(0, writable, buf, writeIndex);
		writeIndex += writable;
		DECIMAL.setLength(0);
	}

	/**
	 * 写入指定位置
	 *
	 * @param <T>       返回值类型
	 * @param generator 写入策略
	 * @param dest      写入目标
	 * @param offset    偏移位置
	 * @return 返回内容
	 * @throws WriteFailedException 写入失败异常
	 */
	public <T> T writeTo(Generator<T> generator, T dest, int offset) throws WriteFailedException {
		int length = bufferIndex << CodecConfig.HIGHEST + writeIndex;
		return generator.generate(dest, offset, buffers, length, bufferIndex, writeIndex);
	}

	/**
	 * 写入一个分割标识
	 *
	 * @param mark 标识值
	 */
	public void writeMark(char mark) {
		ensureWritable(1);
		buf[writeIndex++] = mark;
	}

	/**
	 * 写入boolean值
	 *
	 * @param value boolean值
	 */
	public void write(boolean value) {
		ensureWritable(value ? 4 : 5);
		if (value) {
			buf[writeIndex++] = 't';
			buf[writeIndex++] = 'r';
			buf[writeIndex++] = 'u';
			buf[writeIndex++] = 'e';
		} else {
			buf[writeIndex++] = 'f';
			buf[writeIndex++] = 'a';
			buf[writeIndex++] = 'l';
			buf[writeIndex++] = 's';
			buf[writeIndex++] = 'e';
		}
	}

	/**
	 * 写入int值
	 *
	 * @param value int值
	 */
	public void write(int value) {
		int writable = CodecUtil.length(value);
		ensureWritable(writable);
		writeIndex += writable;
		CodecUtil.writeInt(buf, writeIndex, value);
	}

	/**
	 * 写入long值
	 *
	 * @param value long值
	 */
	public void write(long value) {
		int writable = CodecUtil.length(value);
		ensureWritable(writable);
		writeIndex += writable;
		CodecUtil.writeLong(buf, writeIndex, value);
	}

	/**
	 * 写入float值
	 *
	 * @param value float值
	 */
	public void write(float value) {
		DECIMAL.append(value);
		doWrite();
	}

	/**
	 * 写入double值
	 *
	 * @param value double值
	 */
	public void write(double value) {
		DECIMAL.append(value);
		doWrite();
	}

	/**
	 * 写入{@link String}值
	 *
	 * @param value {@link String}值
	 */
	public void write(String value) {
		if (CodecOptions.WriteUsingUnicode.isOptions(options)) {
			int writable = value.length() * 6 + 2;
			ensureWritable(writable);
			writeStringUnicode(value);
		} else {
			int writable = CodecUtil.length(value) + 2;
			ensureWritable(writable);
			writeStringNormal(value);
		}
	}

	/**
	 * 使用unicode方式写入{@link String}值。
	 *
	 * @param value {@link String}值
	 */
	protected void writeStringUnicode(String value) {
		buf[writeIndex++] = '"';
		for (int i = 0, length = value.length(); i < length; i++) {
			writeUnicode(value.charAt(i));
		}
		buf[writeIndex++] = '"';
	}

	/**
	 * 使用常规方式写入{@link String}值。
	 *
	 * @param value {@link String}值
	 */
	protected void writeStringNormal(String value) {
		buf[writeIndex++] = '"';
		for (int i = 0, length = value.length(); i < length; i++) {
			writeNormal(value.charAt(i));
		}
		buf[writeIndex++] = '"';
	}

	/**
	 * 写入char值和双引号
	 *
	 * @param value char值
	 */
	public void writeWithQuote(char value) {
		boolean unicode = CodecOptions.WriteUsingUnicode.isOptions(options);
		int length = 2;
		if (unicode) {
			length += 6;
		} else {
			length += CodecUtil.length(value);
		}
		ensureWritable(length);
		buf[writeIndex++] = '"';
		if (unicode) {
			writeUnicode(value);
		} else {
			writeNormal(value);
		}
		buf[writeIndex++] = '"';
	}

	/**
	 * 写入{@link String}值
	 *
	 * @param value {@link String}值
	 */
	public final void writeWithQuote(String value) {
		write(value);
	}

	/**
	 * 写入字段
	 *
	 * @param mark 前置标识
	 * @param name 字段信息
	 */
	public void writeName(char mark, char[] name) {
		int writable = 4 + CodecUtil.length(name.length);
		ensureWritable(writable);
		write(mark);
		write('"');
		for (char c : name) {
			writeNormal(c);
		}
		write('"');
		write(':');
	}

	/**
	 * 写入boolean字段
	 *
	 * @param mark  前置标识
	 * @param name  字段信息
	 * @param value boolean值
	 */
	public final void writeValue(char mark, char[] name, boolean value) {
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
	public final void writeValue(char mark, char[] name, char value) {
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
	public final void writeValue(char mark, char[] name, int value) {
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
	public final void writeValue(char mark, char[] name, long value) {
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
	public final void writeValue(char mark, char[] name, float value) {
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
	public final void writeValue(char mark, char[] name, double value) {
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
	public final void writeValue(char mark, char[] name, @Nullable String value) {
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
	public void writeNull() {
		ensureWritable(4);
		buf[writeIndex++] = 'n';
		buf[writeIndex++] = 'u';
		buf[writeIndex++] = 'l';
		buf[writeIndex++] = 'l';
	}

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writable 即将写入的字节数
	 * @return 是否横跨缓冲区。{@code true}横跨缓冲区写入，{@code false}当前缓冲区写入。
	 */
	protected boolean ensureWritable(int writable) {
		if (writable + writeIndex >= capacity) {
			return expandCapacity(writable + capacity);
		}
		return false;
	}

	/**
	 * 缓冲区扩容
	 * <p>
	 * 扩容时有两种方案：
	 * <ul>
	 *     <li>{@link WriterBuffer}缓冲区扩容</li>
	 *     <li>复合缓冲区扩容</li>
	 * </ul>
	 * <p>
	 * 使用{@code 256k}划分，当前容量小于{@code 256k}时，使用当前缓冲区扩容，
	 * 当容量大于{@code 256k}时，使用添加缓冲区扩容。
	 * <p>
	 * 常规缓冲区：
	 * 常规缓冲区中只有一个缓冲区，当对缓冲区进行扩容时，直接将缓冲区容量进行翻倍。
	 * 优点：写入速度快，缺点：大容量缓冲区容量翻倍时对内存造成过大压力。
	 * <p>
	 * 复合缓冲区：
	 * 复合缓冲区中有多个缓冲区，当一个缓冲区中的数据写满时，开始在第二个缓冲区中写入。
	 * 优点：拓容方式更灵活，缺点：跨缓冲区写入时性能下降。
	 *
	 * @param minCapacity 需要的最小容量
	 * @return 是否添加缓冲区。{@code true}添加缓冲区，{@code false}当前缓冲区。
	 */
	protected abstract boolean expandCapacity(int minCapacity);

	/**
	 * 初始化
	 *
	 * @param options 编码选项
	 */
	public void init(long options) {
		this.writeIndex = 0;
		this.bufferIndex = 0;
		this.options = options;
		if (buffers[0] == null) {
			buffers[0] = new char[4096];
		}
		this.buf = buffers[0];
		this.capacity = buf.length;
	}

	/**
	 * 释放缓冲区
	 */
	public abstract void close();
}