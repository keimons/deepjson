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
		if (buf.ensureWritable(writable)) {
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
		if (buf.ensureWritable(1)) {
			buf.safeWrite(mark);
		} else {
			buf.write(mark);
		}
	}

	@Override
	public void write(boolean value) throws IOException {
		if (buf.ensureWritable(value ? 4 : 5)) {
			if (value) {
				buf.safeWrite('t');
				buf.safeWrite('r');
				buf.safeWrite('u');
				buf.safeWrite('e');
			} else {
				buf.safeWrite('f');
				buf.safeWrite('a');
				buf.safeWrite('l');
				buf.safeWrite('s');
				buf.safeWrite('e');
			}
		} else {
			if (value) {
				buf.write('t');
				buf.write('r');
				buf.write('u');
				buf.write('e');
			} else {
				buf.write('f');
				buf.write('a');
				buf.write('l');
				buf.write('s');
				buf.write('e');
			}
		}
	}

	@Override
	public void write(int value) throws IOException {
		int writable = CodecUtil.length(value);
		CodecUtil.writeInt(TEMP, writable, value);
		if (buf.ensureWritable(writable)) {
			for (int i = 0; i < writable; i++) {
				buf.safeWrite(TEMP[i]);
			}
		} else {
			for (int i = 0; i < writable; i++) {
				buf.write(TEMP[i]);
			}
		}
	}

	@Override
	public void write(long value) throws IOException {
		int writable = CodecUtil.length(value);
		CodecUtil.writeLong(TEMP, writable, value);
		if (buf.ensureWritable(writable)) {
			for (int i = 0; i < writable; i++) {
				buf.safeWrite(TEMP[i]);
			}
		} else {
			for (int i = 0; i < writable; i++) {
				buf.write(TEMP[i]);
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
		if (buf.ensureWritable(value.length() * 6 + 2)) {
			buf.safeWrite('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				safeWriteUnicode(value.charAt(i));
			}
			buf.safeWrite('"');
		} else {
			buf.write('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				writeUnicode(value.charAt(i));
			}
			buf.write('"');
		}
	}

	private void writeStringNormal(String value) throws IOException {
		int writable = CodecUtil.length(value) + 2;
		if (buf.ensureWritable(writable)) {
			buf.safeWrite('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				safeWriteNormal(value.charAt(i));
			}
			buf.safeWrite('"');
		} else {
			buf.write('"');
			for (int i = 0, length = value.length(); i < length; i++) {
				writeNormal(value.charAt(i));
			}
			buf.write('"');
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
		if (buf.ensureWritable(length)) {
			buf.safeWrite('"');
			if (unicode) {
				safeWriteUnicode(value);
			} else {
				safeWriteNormal(value);
			}
			buf.safeWrite('"');
		} else {
			buf.write('"');
			if (unicode) {
				writeUnicode(value);
			} else {
				writeNormal(value);
			}
			buf.write('"');
		}
	}

	@Override
	public void writeName(char mark, char[] name) throws IOException {
		int writable = 4 + CodecUtil.length(name);
		if (buf.ensureWritable(writable)) {
			buf.safeWrite(mark);
			buf.safeWrite('"');
			for (char c : name) {
				safeWriteNormal(c);
			}
			buf.safeWrite('"');
			buf.safeWrite(':');
		} else {
			buf.write(mark);
			buf.write('"');
			for (char c : name) {
				writeNormal(c);
			}
			buf.write('"');
			buf.write(':');
		}
	}

	@Override
	public void writeNull() throws IOException {
		if (buf.ensureWritable(4)) {
			buf.safeWrite('n');
			buf.safeWrite('u');
			buf.safeWrite('l');
			buf.safeWrite('l');
		} else {
			buf.write('n');
			buf.write('u');
			buf.write('l');
			buf.write('l');
		}
	}

	@Override
	public void close() throws IOException {
		buf.close();
	}
}