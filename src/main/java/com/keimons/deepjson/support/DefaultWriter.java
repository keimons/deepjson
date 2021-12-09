package com.keimons.deepjson.support;

import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.util.CodecUtil;

import java.io.IOException;

/**
 * 复合缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DefaultWriter extends JsonWriter {

	/**
	 * 临时缓冲区
	 * <p>
	 * 当跨缓冲区写入时，需要先将数值写入临时缓冲区，然后将再将临时缓冲区写入到缓冲区中。
	 * {@code 32}个字符可以容纳包括{@code long}和{@code double}在内的所有数字类
	 * 型的临时写入需求。
	 */
	private final char[] TEMP = new char[64];

	private final StringBuilder DECIMAL = new StringBuilder(64);

	/**
	 * 写入一个字符
	 *
	 * @param value 字符
	 * @throws IOException 写入异常
	 */
	private void write(char value) throws IOException {
		buf.write(value);
	}

	/**
	 * 跨缓冲区的方式写入一个{@code char}字符
	 * <p>
	 * 首先检测当前缓冲区是否可以写入，如果不能，则先拓展缓冲区再写入。
	 *
	 * @param value 写入的字符
	 * @throws IOException 写入异常
	 */
	private void safeWrite(char value) throws IOException {
		buf.safeWrite(value);
	}

	/**
	 * 按照unicode的编码方式写入一个char，所有字符均转码。
	 *
	 * @param value char
	 * @throws IOException 写入异常
	 */
	protected void writeUnicode(char value) throws IOException {
		buf.write('\\');
		buf.write('u');
		buf.write(CHAR_HEX[value >> 12 & 0xF]);
		buf.write(CHAR_HEX[value >> 8 & 0xF]);
		buf.write(CHAR_HEX[value >> 4 & 0xF]);
		buf.write(CHAR_HEX[value & 0xF]);
	}

	/**
	 * 按照unicode的编码方式写入一个char，所有字符均转码。
	 *
	 * @param value char
	 * @throws IOException 写入异常
	 */
	private void safeWriteUnicode(char value) throws IOException {
		buf.safeWrite('\\');
		buf.safeWrite('u');
		buf.safeWrite(CHAR_HEX[value >> 12 & 0xF]);
		buf.safeWrite(CHAR_HEX[value >> 8 & 0xF]);
		buf.safeWrite(CHAR_HEX[value >> 4 & 0xF]);
		buf.safeWrite(CHAR_HEX[value & 0xF]);
	}

	/**
	 * 使用常规方式写入一个char，仅对特殊字符进行转码。
	 *
	 * @param value char
	 * @throws IOException 写入异常
	 */
	private void writeNormal(char value) throws IOException {
		if (value < 256) {
			char[] chars = REPLACEMENT_CHARS[value];
			if (chars == null) {
				buf.write(value);
			} else {
				for (char replace : chars) {
					buf.write(replace);
				}
			}
		} else {
			if (value == 0x2028 || value == 0x2029) { // 0x2028 0x2029
				writeUnicode(value);
			} else {
				buf.write(value);
			}
		}
	}

	/**
	 * 使用常规方式写入一个char，仅对特殊字符进行转码。
	 *
	 * @param value char
	 * @throws IOException 写入异常
	 */
	private void safeWriteNormal(char value) throws IOException {
		if (value < 256) {
			char[] chars = REPLACEMENT_CHARS[value];
			if (chars == null) {
				buf.safeWrite(value);
			} else {
				for (char replace : chars) {
					buf.safeWrite(replace);
				}
			}
		} else {
			if (value == 0x2028 || value == 0x2029) { // 0x2028 0x2029
				safeWriteUnicode(value);
			} else {
				buf.safeWrite(value);
			}
		}
	}

	/**
	 * 将临时内容写入缓冲区中
	 *
	 * @throws IOException 写入异常
	 */
	private void writeTmp() throws IOException {
		int writable = DECIMAL.length();
		if (ensureWritable(writable)) {
			for (int i = 0; i < writable; i++) {
				buf.safeWrite(DECIMAL.charAt(i));
			}
		} else {
			for (int i = 0; i < writable; i++) {
				buf.write(DECIMAL.charAt(i));
			}
		}
		DECIMAL.setLength(0);
	}

	@Override
	public void writeMark(char mark) throws IOException {
		if (ensureWritable(1)) {
			safeWrite(mark);
		} else {
			write(mark);
		}
	}

	@Override
	public void write(boolean value) throws IOException {
		if (ensureWritable(value ? 4 : 5)) {
			if (value) {
				safeWrite('t');
				safeWrite('r');
				safeWrite('u');
				safeWrite('e');
			} else {
				safeWrite('f');
				safeWrite('a');
				safeWrite('l');
				safeWrite('s');
				safeWrite('e');
			}
		} else {
			if (value) {
				write('t');
				write('r');
				write('u');
				write('e');
			} else {
				write('f');
				write('a');
				write('l');
				write('s');
				write('e');
			}
		}
	}

	@Override
	public void write(int value) throws IOException {
		int writable = CodecUtil.length(value);
		CodecUtil.writeInt(TEMP, writable, value);
		if (ensureWritable(writable)) {
			for (int i = 0; i < writable; i++) {
				safeWrite(TEMP[i]);
			}
		} else {
			for (int i = 0; i < writable; i++) {
				write(TEMP[i]);
			}
		}
	}

	@Override
	public void write(long value) throws IOException {
		int writable = CodecUtil.length(value);
		CodecUtil.writeLong(TEMP, writable, value);
		if (ensureWritable(writable)) {
			for (int i = 0; i < writable; i++) {
				safeWrite(TEMP[i]);
			}
		} else {
			for (int i = 0; i < writable; i++) {
				write(TEMP[i]);
			}
		}
	}

	@Override
	public void write(float value) throws IOException {
		DECIMAL.append(value);
		writeTmp();
	}

	@Override
	public void write(double value) throws IOException {
		DECIMAL.append(value);
		writeTmp();
	}

	@Override
	public void write(String value) throws IOException {
		if (CodecOptions.WriteUsingUnicode.isOptions(options)) {
			writeStringUnicode(value);
		} else {
			writeStringNormal(value);
		}
	}

	private void writeStringUnicode(String value) throws IOException {
		if (ensureWritable(value.length() * 6 + 2)) {
			safeWrite('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				safeWriteUnicode(value.charAt(i));
			}
			safeWrite('"');
		} else {
			write('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				writeUnicode(value.charAt(i));
			}
			write('"');
		}
	}

	private void writeStringNormal(String value) throws IOException {
		int writable = CodecUtil.length(value) + 2;
		if (ensureWritable(writable)) {
			safeWrite('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				safeWriteNormal(value.charAt(i));
			}
			safeWrite('"');
		} else {
			write('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				writeNormal(value.charAt(i));
			}
			write('"');
		}
	}

	@Override
	public void writeWithQuote(char value) throws IOException {
		boolean unicode = CodecOptions.WriteUsingUnicode.isOptions(options);
		int length = 2;
		if (unicode) {
			length += 6;
		} else {
			length += CodecUtil.length(value);
		}
		if (ensureWritable(length)) {
			safeWrite('"');
			if (unicode) {
				safeWriteUnicode(value);
			} else {
				safeWriteNormal(value);
			}
			safeWrite('"');
		} else {
			write('"');
			if (unicode) {
				writeUnicode(value);
			} else {
				writeNormal(value);
			}
			write('"');
		}
	}

	@Override
	public void writeName(char mark, char[] name) throws IOException {
		int writable = 4 + CodecUtil.length(name.length);
		if (ensureWritable(writable)) {
			safeWrite(mark);
			safeWrite('"');
			for (char c : name) {
				safeWriteNormal(c);
			}
			safeWrite('"');
			safeWrite(':');
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
	public void writeNull() throws IOException {
		if (ensureWritable(4)) {
			safeWrite('n');
			safeWrite('u');
			safeWrite('l');
			safeWrite('l');
		} else {
			write('n');
			write('u');
			write('l');
			write('l');
		}
	}

	@Override
	public void close() throws IOException {
		buf.close();
	}
}