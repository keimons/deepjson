package com.keimons.deepjson.support.buffer;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.ICharBuffer;
import com.keimons.deepjson.pool.PooledBufferFactory;
import com.keimons.deepjson.util.CodecUtil;

import java.util.Arrays;

/**
 * 复合缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CompositeBuffer extends AbstractBuffer {

	/**
	 * 复合缓冲区初始容量
	 */
	private static final int COMPOSITE_BUF_SIZE = 8;
	/**
	 * 临时缓冲区
	 * <p>
	 * 当跨缓冲区写入时，需要先将数值写入临时缓冲区，然后将再将临时缓冲区写入到缓冲区中。
	 * {@code 32}个字符可以容纳包括{@code long}和{@code double}在内的所有数字类
	 * 型的临时写入需求。
	 */
	private final char[] TEMP = new char[32];
	/**
	 * 复合缓冲区
	 */
	private ICharBuffer[] caches = new ICharBuffer[COMPOSITE_BUF_SIZE];

	public CompositeBuffer() {
		super(COMPOSITE_BUF_SIZE);
	}

	/**
	 * 跨缓冲区的方式写入一个{@code char}字符
	 * <p>
	 * 首先检测当前缓冲区是否可以写入，如果不能，则先拓展缓冲区再写入。
	 *
	 * @param value 写入的字符
	 */
	private void writeLeap(char value) {
		if ((writeIndex >= capacity)) {
			// 切换缓冲区
			bufferIndex++;
			writeIndex = 0;
			buf = buffers[bufferIndex]; // 设置当前缓冲区
			capacity = buf.length; // 设置当前缓冲区容量
		}
		buffers[bufferIndex][writeIndex++] = value;
	}

	/**
	 * 按照unicode的编码方式写入一个char，所有字符均转码。
	 *
	 * @param value char
	 */
	private void writeUnicodeLeap(char value) {
		writeLeap('\\');
		writeLeap('u');
		writeLeap(CHAR_HEX[value >> 12 & 0xF]);
		writeLeap(CHAR_HEX[value >> 8 & 0xF]);
		writeLeap(CHAR_HEX[value >> 4 & 0xF]);
		writeLeap(CHAR_HEX[value & 0xF]);
	}

	/**
	 * 使用常规方式写入一个char，仅对特殊字符进行转码。
	 *
	 * @param value char
	 */
	private void writeNormalLeap(char value) {
		if (value < 256) {
			char[] chars = REPLACEMENT_CHARS[value];
			if (chars == null) {
				writeLeap(value);
			} else {
				for (char replace : chars) {
					writeLeap(replace);
				}
			}
		} else {
			if (value == 0x2028 || value == 0x2029) { // 0x2028 0x2029
				writeUnicodeLeap(value);
			} else {
				writeLeap(value);
			}
		}
	}

	@Override
	public void writeMark(char mark) {
		if (ensureWritable(1)) {
			writeLeap(mark);
		} else {
			buf[writeIndex++] = mark;
		}
	}

	@Override
	public void write(boolean value) {
		if (ensureWritable(value ? 4 : 5)) {
			if (value) {
				writeLeap('t');
				writeLeap('r');
				writeLeap('u');
				writeLeap('e');
			} else {
				writeLeap('f');
				writeLeap('a');
				writeLeap('l');
				writeLeap('s');
				writeLeap('e');
			}
		} else {
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
	}

	@Override
	public void write(int value) {
		int writable = CodecUtil.length(value);
		if (ensureWritable(writable)) {
			CodecUtil.writeInt(TEMP, writable, value);
			for (int i = 0; i < writable; i++) {
				writeLeap(TEMP[i]);
			}
		} else {
			writeIndex += writable;
			CodecUtil.writeInt(buf, writeIndex, value);
		}
	}

	@Override
	public void write(long value) {
		int writable = CodecUtil.length(value);
		if (ensureWritable(writable)) {
			CodecUtil.writeLong(TEMP, writable, value);
			for (int i = 0; i < writable; i++) {
				writeLeap(TEMP[i]);
			}
		} else {
			writeIndex += writable;
			CodecUtil.writeLong(buf, writeIndex, value);
		}
	}

	@Override
	public void write(float value) {
		if (ensureWritable(15)) {
			int writable = decimal.write(value, TEMP, 0);
			for (int i = 0; i < writable; i++) {
				writeLeap(TEMP[i]);
			}
		} else {
			int length = decimal.write(value, buf, writeIndex);
			writeIndex += length;
		}
	}

	@Override
	public void write(double value) {
		if (ensureWritable(24)) {
			int writable = decimal.write(value, TEMP, 0);
			for (int i = 0; i < writable; i++) {
				writeLeap(TEMP[i]);
			}
		} else {
			writeIndex += decimal.write(value, buf, writeIndex);
		}
	}

	@Override
	public void write(String value) {
		if (CodecOptions.WriteUsingUnicode.isOptions(options)) {
			writeStringUnicode(value);
		} else {
			writeStringNormal(value);
		}
	}

	@Override
	protected void writeStringUnicode(String value) {
		if (ensureWritable(value.length() * 6 + 2)) {
			writeLeap('"');
			if (CodecUtil.CHARS) {
				char[] values = (char[]) unsafe.getObject(value, CodecUtil.VALUES_OFFSET_STRING);
				for (char c : values) {
					writeUnicodeLeap(c);
				}
			} else {
				byte coder = unsafe.getByte(value, CodecUtil.CODER_OFFSET_STRING);
				byte[] values = (byte[]) unsafe.getObject(value, CodecUtil.VALUE_OFFSET_STRING);
				if (CodecUtil.isLatin1(coder)) {
					for (byte b : values) {
						writeLeap('\\');
						writeLeap('u');
						writeLeap('0');
						writeLeap('0');
						writeLeap(CHAR_HEX[b >> 4 & 0xF]);
						writeLeap(CHAR_HEX[b & 0xF]);
					}
				} else {
					int i = CodecUtil.BIG_ENCODE ? 0 : 1;
					int j = CodecUtil.BIG_ENCODE ? 1 : 0;
					for (int length = values.length; i < length; i += 2, j += 2) {
						byte hi = values[i];
						byte lo = values[j];
						writeLeap('\\');
						writeLeap('u');
						writeLeap(CHAR_HEX[hi >> 4 & 0xF]);
						writeLeap(CHAR_HEX[hi & 0xF]);
						writeLeap(CHAR_HEX[lo >> 4 & 0xF]);
						writeLeap(CHAR_HEX[lo & 0xF]);
					}
				}
			}
			writeLeap('"');
		} else {
			super.writeStringUnicode(value);
		}
	}

	@Override
	protected void writeStringNormal(String value) {
		int writable = CodecUtil.length(value) + 2;
		if (ensureWritable(writable)) {
			if (CodecUtil.CHARS) {
				writeLeap('"');
				char[] values = (char[]) unsafe.getObject(value, CodecUtil.VALUES_OFFSET_STRING);
				for (char c : values) {
					writeNormalLeap(c);
				}
				writeLeap('"');
			} else {
				writeLeap('"');
				byte coder = unsafe.getByte(value, CodecUtil.CODER_OFFSET_STRING);
				byte[] values = (byte[]) unsafe.getObject(value, CodecUtil.VALUE_OFFSET_STRING);
				if (CodecUtil.isLatin1(coder)) {
					for (byte b : values) {
						writeLeap((char) (b & 0xFF));
					}
				} else {
					int i = CodecUtil.BIG_ENCODE ? 0 : 1;
					int j = CodecUtil.BIG_ENCODE ? 1 : 0;
					for (int length = values.length; i < length; i += 2, j += 2) {
						byte hi = values[i];
						byte lo = values[j];
						if (hi == 0) { // 高8位为0
							char[] chars = REPLACEMENT_CHARS[lo & 0xFF];
							if (chars == null) {
								writeLeap((char) (lo & 0xFF));
							} else {
								for (char c : chars) {
									writeLeap(c);
								}
							}
						} else if (hi == 0x20 && (lo == 0x28 || lo == 0x29)) {
							writeLeap('\\');
							writeLeap('u');
							writeLeap('2');
							writeLeap('0');
							writeLeap('2');
							if (lo == 0x28) {
								writeLeap('8'); // 0x2028
							} else {
								writeLeap('9'); // 0x2029
							}
						} else {
							writeLeap((char) (((hi & 0xFF) << 8) | (lo & 0xFF)));
						}
					}
				}
				writeLeap('"');
			}
		} else {
			super.writeStringNormal(value);
		}
	}

	@Override
	public void writeWithQuote(char value) {
		boolean unicode = CodecOptions.WriteUsingUnicode.isOptions(options);
		int length = 2;
		if (unicode) {
			length += 6;
		} else {
			length += CodecUtil.length(value);
		}
		if (ensureWritable(length)) {
			writeLeap('"');
			if (unicode) {
				writeUnicodeLeap(value);
			} else {
				writeNormalLeap(value);
			}
			writeLeap('"');
		} else {
			buf[writeIndex++] = '"';
			if (unicode) {
				writeUnicode(value);
			} else {
				writeNormal(value);
			}
			buf[writeIndex++] = '"';
		}
	}

	@Override
	public void writeName(char mark, char[] name) {
		int writable = 4 + CodecUtil.length(name.length);
		if (ensureWritable(writable)) {
			writeLeap(mark);
			writeLeap('"');
			for (char c : name) {
				writeNormalLeap(c);
			}
			writeLeap('"');
			writeLeap(':');
		} else {
			write(mark);
			write('"');
			for (char c : name) {
				writeNormal(c);
			}
			write('"');
			write(':');
		}
	}

	@Override
	public void writeNull() {
		if (ensureWritable(4)) {
			writeLeap('n');
			writeLeap('u');
			writeLeap('l');
			writeLeap('l');
		} else {
			buf[writeIndex++] = 'n';
			buf[writeIndex++] = 'u';
			buf[writeIndex++] = 'l';
			buf[writeIndex++] = 'l';
		}
	}

	@Override
	protected boolean expandCapacity(int minCapacity) {
		// 4k -> 16k -> 64k -> 256k
		if (bufferIndex == 0 && capacity < 1 << CodecConfig.HIGHEST) {
			int newCapacity = capacity << 2;
			buf = Arrays.copyOf(buf, newCapacity);
			capacity = newCapacity;
			buffers[0] = buf;
			return false;
		} else {
			int bufferIndex = this.bufferIndex + 1;
			int capacity = this.capacity;
			while (capacity < minCapacity) {
				if (bufferIndex >= buffers.length) {
					// 复合缓冲区容量不足
					buffers = Arrays.copyOf(buffers, buffers.length << 1);
					caches = Arrays.copyOf(caches, caches.length << 1);
				}
				ICharBuffer cache = PooledBufferFactory.borrowBuffer(bufferIndex);
				caches[bufferIndex] = cache;
				char[] newBuffer = cache.get();
				buffers[bufferIndex++] = newBuffer;
				capacity += newBuffer.length;
			}
			return true;
		}
	}

	@Override
	public void close() {
		for (int i = PooledBufferFactory.THREAD_LOCAL_BUFFER_SIZE; i < bufferIndex; i++) {
			PooledBufferFactory.returnBuffer(this.caches[i]);
			this.caches[i] = null;
			this.buffers[i] = null;
		}
	}

	public char[][] getBuffers() {
		return buffers;
	}

	public void setBuffers(char[][] buffers) {
		this.buffers = buffers;
	}
}