package com.keimons.deepjson.support.buffer;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.ExpectedSyntaxException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ArrayUtil;

import java.util.Arrays;

/**
 * Json解码缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonReaderBuffer extends ReaderBuffer {

	protected final static int[] digits = new int[128];
	protected final static char[] CONST_true = new char[]{'t', 'r', 'u', 'e'};
	protected final static char[] CONST_false = new char[]{'f', 'a', 'l', 's', 'e'};
	protected final static char[] CONST_null = new char[]{'n', 'u', 'l', 'l'};

	static {
		// '0' - '9'
		for (int i = 48; i < 58; i++) {
			digits[i] = i - 48;
		}
		// 'A' - 'F'
		for (int i = 65; i < 70; i++) {
			digits[i] = (i - 65) + 10;
		}
		// 'a' - 'f'
		for (int i = 97; i < 102; i++) {
			digits[i] = (i - 97) + 10;
		}
	}

	char[] buf;
	int readerIndex;

	/**
	 * 语法开始位置
	 * <p>
	 * 用于记录语法开始位置，当语法不符合时，抛出异常。
	 *
	 * @see #nextToken() 查找下一个token时更新
	 */
	int startIndex;
	char[] cache = new char[64];
	int writeIndex;
	int markerIndex;
	int length;
	char ch;
	SyntaxToken token;

	public JsonReaderBuffer(String context) {
		buf = context.toCharArray();
		length = buf.length;
	}

	@Override
	public char[] base() {
		return buf;
	}

	public SyntaxToken token() {
		return token;
	}

	@Override
	public SyntaxToken nextToken() {
		while (readerIndex < length) {
			startIndex = readerIndex;
			char ch = buf[readerIndex++];
			switch (ch) {
				case '\u0000': // blank
				case '\u0001': // blank
				case '\u0002': // blank
				case '\u0003': // blank
				case '\u0004': // blank
				case '\u0005': // blank
				case '\u0006': // blank
				case '\u0007': // blank
				case '\u0008': // blank
				case '\u0009': // blank
				case '\n':     // blank 000A
				case '\u000B': // blank
				case '\u000C': // blank
				case '\r':     // blank 000D
				case '\u000E': // blank
				case '\u000F': // blank
				case '\u0010': // blank
				case '\u0011': // blank
				case '\u0012': // blank
				case '\u0013': // blank
				case '\u0014': // blank
				case '\u0015': // blank
				case '\u0016': // blank
				case '\u0017': // blank
				case '\u0018': // blank
				case '\u0019': // blank
				case '\u001A': // blank
				case '\u001B': // blank
				case '\u001C': // blank
				case '\u001D': // blank
				case '\u001E': // blank
				case '\u001F': // blank
				case ' ':  // blank
				case '\u007F':
					break;
				case '/': // // or /*
					skipNodes();
					break;
				case '"': // "context"
					token = SyntaxToken.STRING;
					nextString();
					return token;
				case '+': // +1000 == 1000, what's up?
				case '-':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '.': // .5 == 0.5, what's up?
					token = SyntaxToken.NUMBER;
					nextNumber();
					return token;
				case '[': // [
					token = SyntaxToken.LBRACKET;
					return token;
				case ']': // ]
					token = SyntaxToken.RBRACKET;
					return token;
				case '{': // {
					token = SyntaxToken.LBRACE;
					return token;
				case '}': // }
					token = SyntaxToken.RBRACE;
					return token;
				case ',': // ,
					token = SyntaxToken.COMMA;
					return token;
				case ':': // :
					token = SyntaxToken.COLON;
					return token;
				case 't': // true
				case 'T': // TRUE
				case 'f': // false
				case 'F': // FALSE
				case 'n': // null
				case 'N': // NULL
					nextMarkString();
					return token;
				default:
					token = SyntaxToken.ERROR;
					throw new UnknownSyntaxException(buf, startIndex);
			}
		}
		if (token == SyntaxToken.EOF) {
			// 已经到文件尾了，还在试图继续读取
			throw new UnknownSyntaxException("end of file error");
		} else {
			// 标志文件尾部
			token = SyntaxToken.EOF;
			return token;
		}
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken expect) throws ExpectedSyntaxException {
		if (token == expect) {
			return;
		}
		throw new ExpectedSyntaxException(buf, startIndex, token, expect);
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2) throws ExpectedSyntaxException {
		if (token == expect1 || token == expect2) {
			return;
		}
		throw new ExpectedSyntaxException(buf, startIndex, token, expect1, expect2);
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2, SyntaxToken expect3) throws ExpectedSyntaxException {
		if (token == expect1 || token == expect2 || token == expect3) {
			return;
		}
		throw new ExpectedSyntaxException(buf, startIndex, token, expect1, expect2, expect3);
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken... expects) throws ExpectedSyntaxException {
		if (expects == SyntaxToken.OBJECTS && token.isObject()) {
			return;
		}
		for (SyntaxToken expect : expects) {
			if (expect == token) {
				return;
			}
		}
		throw new ExpectedSyntaxException(buf, startIndex, token, expects);
	}

	/**
	 * 跳过注释
	 */
	private void skipNodes() {
		if (readerIndex >= length) {
			throw new UnknownSyntaxException(buf, startIndex);
		}
		char ch = buf[readerIndex++];
		if (ch == '/') {
			// 行注释 查找行末
			for (; readerIndex < length; readerIndex++) {
				if (buf[readerIndex] == '\n') {
					readerIndex += 1; // jump 1
					return;
				}
			}
		} else if (ch == '*') {
			// 快注释 查找块尾
			for (int limit = length - 1; readerIndex < limit; readerIndex++) {
				if (buf[readerIndex] == '*' && buf[readerIndex + 1] == '/') {
					readerIndex += 2; // jump 2
					return;
				}
			}
		} else {
			// 未知的注释，抛出未知语法异常
			throw new UnknownSyntaxException(buf, markerIndex);
		}
	}

	private void nextString() {
		writeIndex = 0;
		int capacity = cache.length;
		counter:
		{
			// 优化 增加一个limit，针对于合法json是不需要扫描到最后的
			for (int i = readerIndex, limit = length - 1; i < limit; ) {
				if (capacity <= writeIndex) {
					cache = Arrays.copyOf(cache, capacity <<= 1);
				}
				char c = buf[i++];
				if (c == '"') {
					readerIndex = i;
					break counter;
				}
				if (c == '\\') {
					char cmd = buf[i++];
					switch (cmd) {
						case '0':
							cache[writeIndex++] = '\u0000';
							break;
						case '1':
							cache[writeIndex++] = '\u0001';
							break;
						case '2':
							cache[writeIndex++] = '\u0002';
							break;
						case '3':
							cache[writeIndex++] = '\u0003';
							break;
						case '4':
							cache[writeIndex++] = '\u0004';
							break;
						case '5':
							cache[writeIndex++] = '\u0005';
							break;
						case '6':
							cache[writeIndex++] = '\u0006';
							break;
						case '7':
							cache[writeIndex++] = '\u0007';
							break;
						case 'b':
							cache[writeIndex++] = '\u0008';
							break;
						case 't':
							cache[writeIndex++] = '\u0009';
							break;
						case 'n':
							cache[writeIndex++] = '\n'; // 000A
							break;
						case 'v':
							cache[writeIndex++] = '\u000B';
							break;
						case 'f':
							cache[writeIndex++] = '\u000C';
							break;
						case 'r':
							cache[writeIndex++] = '\r'; // 000D
							break;
						case '"':
							cache[writeIndex++] = '"';
							break;
						case '\'':
							cache[writeIndex++] = '\'';
							break;
						case '/':
							cache[writeIndex++] = '/';
							break;
						case '\\':
							cache[writeIndex++] = '\\';
							break;
						case 'x':
							if (length - i < 2) {
								throw new UnknownSyntaxException("incomplete escape character", buf, i - 2);
							}
							char c1 = buf[i++];
							char c2 = buf[i++];
							if (checkNotHex16(c1) || checkNotHex16(c1)) {
								throw new UnknownSyntaxException("illegal hex \\x" + c1 + c2, buf, i - 6);
							}
							cache[writeIndex++] = (char) (digits[c1] << 4 | digits[c2]);
							break;
						case 'u':
							if (length - i < 4) {
								throw new UnknownSyntaxException("incomplete escape character", buf, i - 2);
							}
							char v1 = buf[i++];
							char v2 = buf[i++];
							char v3 = buf[i++];
							char v4 = buf[i++];
							if (checkNotHex16(v1) || checkNotHex16(v2) || checkNotHex16(v3) || checkNotHex16(v4)) {
								throw new UnknownSyntaxException("illegal unicode \\u" + v1 + v2 + v3 + v4, buf, i - 6);
							}
							cache[writeIndex++] = (char) ((v1 << 12) | (v2 << 8) | (v3 << 4) | v4);
							break;
						default:
							throw new UnknownSyntaxException("unknown escape character \\" + cmd, buf, i - 2);
					}
				} else {
					cache[writeIndex++] = c;
				}
			}
			char latest = buf[length - 1];
			if (latest == '"') {
				// 更新读取位置，对于合法的json，最后一个应该是'}'或']'，此操作可能多于。
				readerIndex = length;
			} else if (latest == '\\') {
				// 最后一个字符如果是'\\'代表不完整的转义字符
				throw new UnknownSyntaxException("incomplete escape character", buf, markerIndex);
			} else {
				throw new UnknownSyntaxException("unclose string", buf, markerIndex);
			}
		}
	}

	@Override
	public int valueHashcode() {
		return ArrayUtil.hashcode(cache, 0, writeIndex);
	}

	@Override
	public String stringValue() {
		return new String(cache, 0, writeIndex);
	}

	@Override
	public Number adaptiveNumber() {
		boolean isDecimal = false;
		for (int i = 0; i < writeIndex; i++) {
			char c = cache[i];
			switch (c) {
				case 'f':
				case 'F':
				case 'd':
				case 'D':
				case '.':
				case 'e':
				case 'E':
					isDecimal = true;
					break;
			}
		}
		if (isDecimal) {
			return doubleValue();
		} else {
			return longValue();
		}
	}

	@Override
	public char charValue() {
		return cache[0];
	}

	@Override
	public int intValue() {
		// TODO using CharSequence
		return Integer.parseInt(new String(cache, 0, writeIndex));
	}

	@Override
	public long longValue() {
		if (writeIndex > 0) {
			char last = cache[writeIndex - 1];
			if (last == 'L' || last == 'l') {
				writeIndex -= 1;
			}
		}
		// TODO using CharSequence
		return Long.parseLong(new String(cache, 0, writeIndex));
	}

	@Override
	public float floatValue() {
		return Float.parseFloat(new String(cache, 0, writeIndex));
	}

	@Override
	public double doubleValue() {
		return Double.parseDouble(new String(cache, 0, writeIndex));
	}

	@Override
	public boolean is$Id() {
		if (writeIndex < 5) {
			return false;
		}
		return cache[0] == '$' && cache[1] == 'i' && cache[2] == 'd' && cache[3] == ':';
	}

	@Override
	public int get$Id() {
		return Integer.parseInt(new String(cache, 4, writeIndex - 4));
	}

	@Override
	public boolean checkPutId() {
		if (writeIndex != 3) {
			return false;
		}
		return cache[0] == '@' && cache[1] == 'i' && cache[2] == 'd';
	}

	@Override
	public boolean checkGetType() {
		if (writeIndex != 5) {
			return false;
		}
		return cache[0] == '$' && cache[1] == 't' && cache[2] == 'y' && cache[3] == 'p' && cache[4] == 'e';
	}

	@Override
	public boolean checkGetValue() {
		if (writeIndex != 6) {
			return false;
		}
		return cache[0] == '$' &&
				cache[1] == 'v' &&
				cache[2] == 'a' &&
				cache[3] == 'l' &&
				cache[4] == 'u' &&
				cache[5] == 'e';
	}

	@Override
	public void close() {
		// TODO clear something
	}

	/**
	 * 读取下一个标价字符串
	 * <p>
	 * 标记字符串包括：true，false，null和它们的各种大小写。
	 */
	private void nextMarkString() {
		writeIndex = 0;
		// 预读5个字节
		for (int i = readerIndex - 1, limit = Math.min(i + 5, length); i < limit; i++) {
			char x = buf[i];
			x = x < 97 ? (char) (x + 32) : x;
			cache[writeIndex++] = x;
		}
		switch (cache[0]) {
			case 't':
				if (ArrayUtil.isSame(cache, CONST_true, 4)) {
					token = SyntaxToken.TRUE;
					readerIndex += 3;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", buf, startIndex);
				}
			case 'f':
				if (ArrayUtil.isSame(cache, CONST_false, 5)) {
					token = SyntaxToken.FALSE;
					readerIndex += 4;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", buf, startIndex);
				}
			case 'n':
				if (ArrayUtil.isSame(cache, CONST_null, 4)) {
					token = SyntaxToken.NULL;
					readerIndex += 3;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", buf, startIndex);
				}
			default:
				throw new UnknownSyntaxException("unknown mark", buf, startIndex);
		}
	}

	private void nextNumber() {
		writeIndex = 0;
		int capacity = cache.length;
		for (int i = readerIndex - 1; i < length; i++) {
			char c = buf[i];
			if (checkNumber(c)) {
				if (writeIndex > 65535) {
					throw new UnknownSyntaxException("number overflow", buf, startIndex);
				}
				if (capacity <= writeIndex) {
					cache = Arrays.copyOf(cache, cache.length << 1);
					capacity = cache.length;
				}
				cache[writeIndex++] = c;
			} else {
				readerIndex = i;
				return;
			}
		}
		readerIndex = length;
	}

	private boolean checkNumber(char c) {
		// TABLE SWITCH
		switch (c) {
			case '+':
			case '-':
			case '.': // decimal
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'f': // float
			case 'F': // float
			case 'd': // double
			case 'D': // double
			case 'l': // long
			case 'L': // long
			case 'e': // scientific notation
			case 'E': // scientific notation
				return true;
			default:
				return false;
		}
	}

	/**
	 * 检查一个字符是否不为16进制字符
	 *
	 * @param c 被检查的字符
	 * @return 是否不为16进制
	 */
	private boolean checkNotHex16(char c) {
		// TABLE SWITCH
		switch (c) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				return false;
			default:
				return true;
		}
	}

	private void ensureWritable(int minCapacity) {
		if (cache.length < minCapacity) {
			cache = Arrays.copyOf(cache, cache.length << 1);
		}
	}

	public int readerIndex() {
		return readerIndex;
	}

	@Override
	public int markerIndex() {
		return markerIndex;
	}

	public void markReaderIndex() {
		markerIndex = readerIndex;
	}

	public void resetReaderIndex() {
		this.token = null;
		this.readerIndex = markerIndex;
	}

	@Override
	public void resetReaderIndex(int readerIndex) {
		this.token = null;
		this.readerIndex = readerIndex;
	}
}