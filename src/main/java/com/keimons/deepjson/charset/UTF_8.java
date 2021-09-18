package com.keimons.deepjson.charset;

import com.keimons.deepjson.IAdapter;
import com.keimons.deepjson.IArrayEncoder;

/**
 * UTF8编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UTF_8 implements IArrayEncoder<IAdapter> {

	@Override
	public int length(char[][] buffers, int bufferIndex, int writeIndex) {
		int length = 0;
		boolean dc = false;
		for (int i = 0; i < bufferIndex; i++) {
			char[] cur = buffers[i];
			char last = cur[cur.length - 1];
			if ('\uD800' <= last && last <= ('\uDFFF')) {
				char first = buffers[i + 1][0];
				length += length(cur, dc ? 1 : 0, cur.length, true, first);
				dc = true;
			} else {
				length += length(cur, dc ? 1 : 0, cur.length, false, '?');
				dc = false;
			}
		}
		length += length(buffers[bufferIndex], dc ? 1 : 0, writeIndex, false, '?');
		return length;
	}

	@Override
	public int encode(char[][] buffers, int bufferIndex, int writeIndex, byte[] dest) {
		int length = 0;
		boolean dc = false;
		for (int i = 0; i < bufferIndex; i++) {
			char[] cur = buffers[i];
			char last = cur[cur.length - 1];
			if ('\uD800' <= last && last <= ('\uDFFF')) {
				char first = buffers[i + 1][0];
				length += encode(cur, dc ? 1 : 0, cur.length, true, first, dest, length);
				dc = true;
			} else {
				length += encode(cur, dc ? 1 : 0, cur.length, false, '?', dest, length);
				dc = false;
			}
		}
		length += encode(buffers[bufferIndex], dc ? 1 : 0, writeIndex, false, '?', dest, length);
		return length;
	}

	@Override
	public int encode(char[][] buffers, int bufferIndex, int writeIndex, IAdapter dest) {
		boolean dc = false;
		for (int i = 0; i < bufferIndex; i++) {
			char[] cur = buffers[i];
			char last = cur[cur.length - 1];
			if ('\uD800' <= last && last <= ('\uDFFF')) {
				char first = buffers[i + 1][0];
				encode(cur, dc ? 1 : 0, cur.length, true, first, dest);
				dc = true;
			} else {
				encode(cur, dc ? 1 : 0, cur.length, false, '?', dest);
				dc = false;
			}
		}
		encode(buffers[bufferIndex], dc ? 1 : 0, writeIndex, false, '?', dest);
		return 0;
	}

	private int length(final char[] chars, int index, int length, boolean hasNext, char last) {
		int size = 0;
		int calcLength = length + (hasNext ? 1 : 0);
		while (index < length) {
			char c = chars[index++];
			if (c < 0x80) {
				size += 1;
			} else if (c < 0x800) {
				size += 2;
			} else if ('\uD800' <= c && c <= ('\uDFFF')) { // 针对于部分char不能表示的字符，采用双char编码
				int start = index - 1; // 双char编码的开始位置
				if (c <= '\uDBFF' && calcLength - start >= 2) { // 双char编码的第一个char必须位于 '\uD800' 到 '\uDBFF' 之间
					// 复合缓冲区带来的问题，如果最后一个char正好是双char编码，那么需要继续读取下一个缓冲区的第一个char
					char next = index >= length ? last : chars[index];
					if ('\uDC00' <= next && next <= '\uDFFF') {
						// 第二个char有效
						size += 4;
						++index;
					} else {
						// 第二个char无效
						size += 1;
					}
				} else {
					// 第一个char无效
					size += 1;
				}
			} else {
				size += 3;
			}
		}
		return size;
	}

	private int encode(char[] chars, int index, int length, boolean hasNext, char last, byte[] bytes, int offset) {
		int i = offset;
		int calcLength = length + (hasNext ? 1 : 0);
		while (index < length) {
			char c = chars[index++];
			if (c < 0x80) {
				bytes[i++] = (byte) c;
			} else if (c < 0x800) {
				bytes[i++] = (byte) (0xC0 | c >> 6);
				bytes[i++] = (byte) (0x80 | c & 0x3F);
			} else if ('\uD800' <= c && c <= ('\uDFFF')) { // 针对于部分char不能表示的字符，采用双char编码
				final int unicode;
				int start = index - 1; // 双char编码的开始位置
				if (c <= '\uDBFF') { // 双char编码的第一个char必须位于 '\uD800' 到 '\uDBFF' 之间
					if (calcLength - start < 2) {
						// 没有第二个char了
						unicode = -1;
					} else {
						// 复合缓冲区带来的问题，如果最后一个char正好是双char编码，那么需要继续读取下一个缓冲区的第一个char
						char next = index >= length ? last : chars[index];
						if ('\uDC00' <= next && next <= '\uDFFF') {
							// 第二个char有效
							unicode = ((c << 10) + next) + (0x010000 - ('\uD800' << 10) - '\uDC00');
						} else {
							// 第二个char无效
							unicode = -1;
						}
					}
				} else {
					// 第一个char无效
					unicode = -1;
				}
				if (unicode < 0) {
					bytes[i++] = (byte) '?'; // 解析错误 应该是双char的，但是只有一个合法
				} else {
					bytes[i++] = (byte) (0xF0 | unicode >> 18);
					bytes[i++] = (byte) (0x80 | unicode >> 12 & 0x3F);
					bytes[i++] = (byte) (0x80 | unicode >> 6 & 0x3F);
					bytes[i++] = (byte) (0x80 | unicode & 0x3F);
					++index;
				}
			} else {
				bytes[i++] = (byte) (0xE0 | c >> 12);
				bytes[i++] = (byte) (0x80 | c >> 6 & 0x3F);
				bytes[i++] = (byte) (0x80 | c & 0x3F);
			}
		}
		return i - offset;
	}

	private void encode(char[] chars, int index, int length, boolean hasNext, char last, IAdapter adapter) {
		int calcLength = length + (hasNext ? 1 : 0);
		while (index < length) {
			char c = chars[index++];
			if (c < 0x80) {
				adapter.writeByte(c);
			} else if (c < 0x800) {
				adapter.writeByte(0xC0 | c >> 6);
				adapter.writeByte(0x80 | c & 0x3F);
			} else if ('\uD800' <= c && c <= ('\uDFFF')) { // 针对于部分char不能表示的字符，采用双char编码
				final int unicode;
				int start = index - 1; // 双char编码的开始位置
				if (c <= '\uDBFF') { // 双char编码的第一个char必须位于 '\uD800' 到 '\uDBFF' 之间
					if (calcLength - start < 2) {
						// 没有第二个char了
						unicode = -1;
					} else {
						// 复合缓冲区带来的问题，如果最后一个char正好是双char编码，那么需要继续读取下一个缓冲区的第一个char
						char next = index >= length ? last : chars[index];
						if ('\uDC00' <= next && next <= '\uDFFF') {
							// 第二个char有效
							unicode = ((c << 10) + next) + (0x010000 - ('\uD800' << 10) - '\uDC00');
						} else {
							// 第二个char无效
							unicode = -1;
						}
					}
				} else {
					// 第一个char无效
					unicode = -1;
				}
				if (unicode < 0) {
					adapter.writeByte('?'); // 解析错误 应该是双char的，但是只有一个合法
				} else {
					adapter.writeByte(0xF0 | unicode >> 18);
					adapter.writeByte(0x80 | unicode >> 12 & 0x3F);
					adapter.writeByte(0x80 | unicode >> 6 & 0x3F);
					adapter.writeByte(0x80 | unicode & 0x3F);
					++index; // 双char中两个都合法，跳过一个
				}
			} else {
				adapter.writeByte(0xE0 | c >> 12);
				adapter.writeByte(0x80 | c >> 6 & 0x3F);
				adapter.writeByte(0x80 | c & 0x3F);
			}
		}
	}

	private <T> int encode(char[] chars, int index, int length,
						   boolean hasNext, char last,
						   Consumer<T> consumer, T dest, int offset) {
		int i = offset;
		int calcLength = length + (hasNext ? 1 : 0);
		while (index < length) {
			char c = chars[index++];
			if (c < 0x80) {
				consumer.accept(dest, i++, c);
			} else if (c < 0x800) {
				consumer.accept(dest, i++, 0xC0 | c >> 6);
				consumer.accept(dest, i++, 0x80 | c & 0x3F);
			} else if ('\uD800' <= c && c <= ('\uDFFF')) { // 针对于部分char不能表示的字符，采用双char编码
				final int unicode;
				int start = index - 1; // 双char编码的开始位置
				if (c <= '\uDBFF') { // 双char编码的第一个char必须位于 '\uD800' 到 '\uDBFF' 之间
					if (calcLength - start < 2) {
						// 没有第二个char了
						unicode = -1;
					} else {
						// 复合缓冲区带来的问题，如果最后一个char正好是双char编码，那么需要继续读取下一个缓冲区的第一个char
						char next = index >= length ? last : chars[index];
						if ('\uDC00' <= next && next <= '\uDFFF') {
							// 第二个char有效
							unicode = ((c << 10) + next) + (0x010000 - ('\uD800' << 10) - '\uDC00');
						} else {
							// 第二个char无效
							unicode = -1;
						}
					}
				} else {
					// 第一个char无效
					unicode = -1;
				}
				if (unicode < 0) {
					consumer.accept(dest, i++, '?'); // 解析错误 应该是双char的，但是只有一个合法
				} else {
					consumer.accept(dest, i++, 0xF0 | unicode >> 18);
					consumer.accept(dest, i++, 0x80 | unicode >> 12 & 0x3F);
					consumer.accept(dest, i++, 0x80 | unicode >> 6 & 0x3F);
					consumer.accept(dest, i++, 0x80 | unicode & 0x3F);
					++index;
				}
			} else {
				consumer.accept(dest, i++, 0xE0 | c >> 12);
				consumer.accept(dest, i++, 0x80 | c >> 6 & 0x3F);
				consumer.accept(dest, i++, 0x80 | c & 0x3F);
			}
		}
		return i - offset;
	}
}