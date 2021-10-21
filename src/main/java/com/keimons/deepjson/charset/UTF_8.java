package com.keimons.deepjson.charset;

import com.keimons.deepjson.IConverter;
import com.keimons.deepjson.ITranscoder;

/**
 * UTF8编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class UTF_8 implements ITranscoder {

	@Override
	public int length(char[][] buffers, int length, int bufferIndex, int writeIndex) {
		int size = 0;
		boolean dc = false;
		for (int i = 0; i < bufferIndex; i++) {
			char[] cur = buffers[i];
			char last = cur[cur.length - 1];
			if ('\uD800' <= last && last <= ('\uDFFF')) {
				char first = buffers[i + 1][0];
				size += length(cur, dc ? 1 : 0, cur.length, true, first);
				dc = true;
			} else {
				size += length(cur, dc ? 1 : 0, cur.length, false, '?');
				dc = false;
			}
		}
		size += length(buffers[bufferIndex], dc ? 1 : 0, writeIndex, false, '?');
		return size;
	}

	@Override
	public <T> int encode(char[][] buffers, int bufferIndex, int writeIndex, IConverter<T> converter, T dest, int offset) {
		int length = offset;
		boolean dc = false;
		for (int i = 0; i < bufferIndex; i++) {
			char[] cur = buffers[i];
			char last = cur[cur.length - 1];
			if ('\uD800' <= last && last <= ('\uDFFF')) {
				char first = buffers[i + 1][0];
				length += encode(cur, dc ? 1 : 0, cur.length, true, first, converter, dest, length);
				dc = true;
			} else {
				length += encode(cur, dc ? 1 : 0, cur.length, false, '?', converter, dest, length);
				dc = false;
			}
		}
		length += encode(buffers[bufferIndex], dc ? 1 : 0, writeIndex, false, '?', converter, dest, length);
		return length - offset;
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

	private <T> int encode(char[] chars, int index, int length,
						   boolean hasNext, char last,
						   IConverter<T> consumer, T dest, int offset) {
		int i = offset;
		int calcLength = length + (hasNext ? 1 : 0);
		while (index < length) {
			char c = chars[index++];
			if (c < 0x80) {
				consumer.writeByte(dest, i++, c);
			} else if (c < 0x800) {
				consumer.writeByte(dest, i++, 0xC0 | c >> 6);
				consumer.writeByte(dest, i++, 0x80 | c & 0x3F);
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
					consumer.writeByte(dest, i++, '?'); // 解析错误 应该是双char的，但是只有一个合法
				} else {
					consumer.writeByte(dest, i++, 0xF0 | unicode >> 18);
					consumer.writeByte(dest, i++, 0x80 | unicode >> 12 & 0x3F);
					consumer.writeByte(dest, i++, 0x80 | unicode >> 6 & 0x3F);
					consumer.writeByte(dest, i++, 0x80 | unicode & 0x3F);
					++index;
				}
			} else {
				consumer.writeByte(dest, i++, 0xE0 | c >> 12);
				consumer.writeByte(dest, i++, 0x80 | c >> 6 & 0x3F);
				consumer.writeByte(dest, i++, 0x80 | c & 0x3F);
			}
		}
		return i - offset;
	}
}