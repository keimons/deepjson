package com.keimons.deepjson.support;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.util.ArrayUtil;
import com.keimons.deepjson.util.CodecUtil;

/**
 * Json解码缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DefaultReader extends JsonReader {

	protected final static int[] digits = new int[128];
	protected final static char[] CONST_true = new char[]{'t', 'r', 'u', 'e'};
	protected final static char[] CONST_false = new char[]{'f', 'a', 'l', 's', 'e'};
	protected final static char[] CONST_null = new char[]{'n', 'u', 'l', 'l'};
	protected final static char[] CONST_NaN = new char[]{'n', 'a', 'n'};
	protected final static char[] CONST_Infinity = new char[]{'I', 'n', 'f', 'i', 'n', 'i', 't', 'y'};

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

	CharSequence base;

	int readerIndex;

	/**
	 * 语法开始位置
	 * <p>
	 * 用于记录语法开始位置，当语法不符合时，抛出异常。
	 *
	 * @see #nextToken() 查找下一个token时更新
	 */
	int startIndex;

	int markerIndex;

	final int limit;

	SyntaxToken token;

	Buffer buf = new Buffer();

	public DefaultReader(String context) {
		base = context;
		limit = base.length();
	}

	@Override
	public Buffer buffer() {
		return buf;
	}

	public SyntaxToken token() {
		return token;
	}

	@Override
	public SyntaxToken nextToken() {
		while (readerIndex < limit) {
			startIndex = readerIndex;
			char ch = base.charAt(readerIndex++);
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
				case 'i':
				case 'I':
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
				case 'N': // NULL / NaN
					nextMarkString();
					return token;
				default:
					token = SyntaxToken.ERROR;
					throw new UnknownSyntaxException(base, startIndex);
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
		throw new ExpectedSyntaxException(base, startIndex, token, expect);
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2) throws ExpectedSyntaxException {
		if (token == expect1 || token == expect2) {
			return;
		}
		throw new ExpectedSyntaxException(base, startIndex, token, expect1, expect2);
	}

	@Override
	public void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2, SyntaxToken expect3) throws ExpectedSyntaxException {
		if (token == expect1 || token == expect2 || token == expect3) {
			return;
		}
		throw new ExpectedSyntaxException(base, startIndex, token, expect1, expect2, expect3);
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
		throw new ExpectedSyntaxException(base, startIndex, token, expects);
	}

	/**
	 * 跳过注释
	 */
	private void skipNodes() {
		if (readerIndex >= limit) {
			throw new UnknownSyntaxException(base, startIndex);
		}
		char ch = base.charAt(readerIndex++);
		if (ch == '/') {
			// 行注释 查找行末
			for (; readerIndex < limit; readerIndex++) {
				if (base.charAt(readerIndex) == '\n') {
					readerIndex += 1; // jump 1
					return;
				}
			}
		} else if (ch == '*') {
			// 快注释 查找块尾
			for (int limit = this.limit - 1; readerIndex < limit; readerIndex++) {
				if (base.charAt(readerIndex) == '*' && base.charAt(readerIndex + 1) == '/') {
					readerIndex += 2; // jump 2
					return;
				}
			}
		} else {
			// 未知的注释，抛出未知语法异常
			throw new UnknownSyntaxException(base, markerIndex);
		}
	}

	private void nextString() {
		buf.reset();
		counter:
		{
			// 优化 增加一个limit，针对于合法json是不需要扫描到最后的
			for (int i = readerIndex, limit = this.limit - 1; i < limit; ) {
				char c = base.charAt(i++);
				if (c == '"') {
					readerIndex = i;
					break counter;
				}
				if (c == '\\') {
					char cmd = base.charAt(i++);
					switch (cmd) {
						case '0':
							buf.write('\u0000');
							break;
						case '1':
							buf.write('\u0001');
							break;
						case '2':
							buf.write('\u0002');
							break;
						case '3':
							buf.write('\u0003');
							break;
						case '4':
							buf.write('\u0004');
							break;
						case '5':
							buf.write('\u0005');
							break;
						case '6':
							buf.write('\u0006');
							break;
						case '7':
							buf.write('\u0007');
							break;
						case 'b':
							buf.write('\u0008');
							break;
						case 't':
							buf.write('\u0009');
							break;
						case 'n':
							buf.write('\n'); // 000A
							break;
						case 'v':
							buf.write('\u000B');
							break;
						case 'f':
							buf.write('\u000C');
							break;
						case 'r':
							buf.write('\r'); // 000D
							break;
						case '"':
							buf.write('"');
							break;
						case '\'':
							buf.write('\'');
							break;
						case '/':
							buf.write('/');
							break;
						case '\\':
							buf.write('\\');
							break;
						case 'x':
							if (this.limit - i < 2) {
								throw new UnknownSyntaxException("incomplete escape character", base, i - 2);
							}
							char c1 = base.charAt(i++);
							char c2 = base.charAt(i++);
							if (checkNotHex16(c1) || checkNotHex16(c1)) {
								throw new UnknownSyntaxException("illegal hex \\x" + c1 + c2, base, i - 6);
							}
							buf.write((char) (digits[c1] << 4 | digits[c2]));
							break;
						case 'u':
							if (this.limit - i < 4) {
								throw new UnknownSyntaxException("incomplete escape character", base, i - 2);
							}
							char v1 = base.charAt(i++);
							char v2 = base.charAt(i++);
							char v3 = base.charAt(i++);
							char v4 = base.charAt(i++);
							if (checkNotHex16(v1) || checkNotHex16(v2) || checkNotHex16(v3) || checkNotHex16(v4)) {
								throw new UnknownSyntaxException("illegal unicode \\u" + v1 + v2 + v3 + v4, base, i - 6);
							}
							buf.write((char) ((digits[v1] << 12) | (digits[v2] << 8) | (digits[v3] << 4) | digits[v4]));
							break;
						default:
							throw new UnknownSyntaxException("unknown escape character \\" + cmd, base, i - 2);
					}
				} else {
					buf.write(c);
				}
			}
			char latest = base.charAt(limit - 1);
			if (latest == '"') {
				// 更新读取位置，对于合法的json，最后一个应该是'}'或']'，此操作可能多于。
				readerIndex = limit;
			} else if (latest == '\\') {
				// 最后一个字符如果是'\\'代表不完整的转义字符
				throw new UnknownSyntaxException("incomplete escape character", base, markerIndex);
			} else {
				throw new UnknownSyntaxException("unclose string", base, markerIndex);
			}
		}
	}

	@Override
	public String stringValue() {
		return new String(buf.base(), 0, buf.size());
	}

	@Override
	public Number adaptiveNumber() {
		boolean isDecimal = false;
		for (int i = 0; i < buf.size(); i++) {
			char c = buf.charAt(i);
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
	public boolean booleanValue() {
		if (token == SyntaxToken.TRUE) {
			return Boolean.TRUE;
		}
		if (token == SyntaxToken.FALSE) {
			return Boolean.FALSE;
		}
		return buf.size() == 4 &&
				(buf.charAt(0) == 't' || buf.charAt(0) == 'T') &&
				(buf.charAt(1) == 'r' || buf.charAt(1) == 'R') &&
				(buf.charAt(2) == 'u' || buf.charAt(2) == 'U') &&
				(buf.charAt(3) == 'e' || buf.charAt(3) == 'E');
	}

	@Override
	public byte byteValue() {
		int i = CodecUtil.readInt(buf.base(), 0, buf.size());
		if (i < Byte.MIN_VALUE || i > Byte.MAX_VALUE) {
			String msg = "Value out of range. Value:\"" + new String(buf.base(), 0, buf.size()) + "\"";
			throw new NumberFormatException(msg);
		}
		return (byte) i;
	}

	@Override
	public short shortValue() {
		int i = CodecUtil.readInt(buf.base(), 0, buf.size());
		if (i < Short.MIN_VALUE || Short.MAX_VALUE < i) {
			String msg = "Value out of range. Value:\"" + new String(buf.base(), 0, buf.size()) + "\"";
			throw new NumberFormatException(msg);
		}
		return (short) i;
	}

	@Override
	public char charValue() {
		return buf.charAt(0);
	}

	@Override
	public int intValue() {
		return CodecUtil.readInt(buf.base(), 0, buf.size());
	}

	@Override
	public long longValue() {
		if (buf.size() > 0) {
			char last = buf.charAt(buf.size() - 1);
			if (last == 'L' || last == 'l') {
				buf.writerIndex(buf.size() - 1);
			}
		}
		return CodecUtil.readLong(buf.base(), 0, buf.size());
	}

	@Override
	public float floatValue() {
		return Float.parseFloat(new String(buf.base(), 0, buf.size()));
	}

	@Override
	public double doubleValue() {
		return Double.parseDouble(new String(buf.base(), 0, buf.size()));
	}

	@Override
	public boolean check$Id() {
		if (buf.size() < 5) {
			return false;
		}
		return buf.charAt(0) == '$' && buf.charAt(1) == 'i' && buf.charAt(2) == 'd' && buf.charAt(3) == ':';
	}

	@Override
	public int get$Id() {
		return CodecUtil.readInt(buf.base(), 4, buf.size());
	}

	@Override
	public boolean check$Type() {
		if (buf.size() < 7) {
			return false;
		}
		return buf.charAt(0) == '$' &&
				buf.charAt(1) == 't' &&
				buf.charAt(2) == 'y' &&
				buf.charAt(3) == 'p' &&
				buf.charAt(4) == 'e' &&
				buf.charAt(5) == ':';
	}

	@Override
	public String get$Type() {
		int i = buf.indexOf(',');
		return new String(buf.base(), 6, (i == -1 ? buf.size() : i) - 6);
	}

	@Override
	public boolean checkAtId() {
		int i = buf.indexOf(',');
		if (i == -1) {
			if (buf.size() < 5) {
				return false;
			}
			return buf.charAt(0) == '@' && buf.charAt(1) == 'i' && buf.charAt(2) == 'd' && buf.charAt(3) == ':';
		} else {
			if (buf.size() < i + 6) {
				return false;
			}
			return buf.charAt(i + 1) == '@' && buf.charAt(i + 2) == 'i' && buf.charAt(i + 3) == 'd' && buf.charAt(i + 4) == ':';
		}
	}

	@Override
	public int getAtId() {
		int i = buf.indexOf(',');
		if (i == -1) {
			return Integer.parseInt(new String(buf.base(), 4, buf.size() - 4));
		} else {
			return Integer.parseInt(new String(buf.base(), i + 5, buf.size() - i - 5));
		}
	}

	@Override
	public boolean checkPutId() {
		if (buf.size() != 3) {
			return false;
		}
		return buf.charAt(0) == '@' && buf.charAt(1) == 'i' && buf.charAt(2) == 'd';
	}

	@Override
	public boolean checkGetType() {
		if (buf.size() != 5) {
			return false;
		}
		return buf.charAt(0) == '$' &&
				buf.charAt(1) == 't' &&
				buf.charAt(2) == 'y' &&
				buf.charAt(3) == 'p' &&
				buf.charAt(4) == 'e';
	}

	@Override
	public boolean checkGetValue() {
		if (buf.size() != 6) {
			return false;
		}
		return buf.charAt(0) == '$' &&
				buf.charAt(1) == 'v' &&
				buf.charAt(2) == 'a' &&
				buf.charAt(3) == 'l' &&
				buf.charAt(4) == 'u' &&
				buf.charAt(5) == 'e';
	}

	@Override
	public void close() {
		// TODO clear something
	}

	/**
	 * 读取下一个标价字符串
	 * <p>
	 * 标记字符串包括：true，false，null,NaN和它们的各种大小写。
	 */
	private void nextMarkString() {
		buf.reset();
		// 预读5个字节
		for (int i = readerIndex - 1, limit = Math.min(i + 5, this.limit); i < limit; i++) {
			char x = base.charAt(i);
			x = x < 97 ? (char) (x + 32) : x;
			buf.write(x);
		}
		switch (buf.charAt(0)) {
			case 't':
				if (ArrayUtil.isSame(buf.base(), CONST_true, 4)) {
					token = SyntaxToken.TRUE;
					readerIndex += 3;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", base, startIndex);
				}
			case 'f':
				if (ArrayUtil.isSame(buf.base(), CONST_false, 5)) {
					token = SyntaxToken.FALSE;
					readerIndex += 4;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", base, startIndex);
				}
			case 'n':
				if (ArrayUtil.isSame(buf.base(), CONST_null, 4)) {
					token = SyntaxToken.NULL;
					readerIndex += 3;
					return;
				} else if (ArrayUtil.isSame(buf.base(), CONST_NaN, 3)) {
					buf.reset();
					token = SyntaxToken.NUMBER;
					buf.write('N');
					buf.write('a');
					buf.write('N');
					readerIndex += 2;
					return;
				} else {
					throw new UnknownSyntaxException("unknown mark", base, startIndex);
				}
			default:
				throw new UnknownSyntaxException("unknown mark", base, startIndex);
		}
	}

	private void nextNumber() {
		buf.reset();
		for (int i = readerIndex - 1; i < limit; i++) {
			char c = base.charAt(i);
			if (checkNumber(c)) {
				if (buf.size() > 65535) {
					throw new UnknownSyntaxException("number overflow", base, startIndex);
				}
				buf.write(c);
			} else if (checkInfinity(i, c)) {
				i += 7;
				buf.write('I'); // Infinity
				buf.write('n');
				buf.write('f');
				buf.write('i');
				buf.write('n');
				buf.write('i');
				buf.write('t');
				buf.write('y');
			} else if (checkNaN(i, c)) {
				i += 2;
				buf.write('N');
				buf.write('a');
				buf.write('N');
			} else {
				readerIndex = i;
				return;
			}
		}
		readerIndex = limit;
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

	private boolean checkInfinity(int i, char c) {
		if (c == 'i' || c == 'I') {
			i++;
			// Infinity
			for (int length = i + 7, j = 1; i < length && i < limit; i++, j++) {
				char x = base.charAt(i);
				x = x < 97 ? (char) (x + 32) : x;
				if (x != CONST_Infinity[j]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean checkNaN(int i, char c) {
		if (c == 'n' || c == 'N') {
			i++;
			// Infinity
			for (int length = i + 2, j = 1; i < length && i < limit; i++, j++) {
				char x = base.charAt(i);
				x = x < 97 ? (char) (x + 32) : x;
				if (x != CONST_NaN[j]) {
					return false;
				}
			}
			return true;
		}
		return false;
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

	public int readerIndex() {
		return readerIndex;
	}

	public void markReaderIndex() {
		markerIndex = startIndex;
	}

	public void resetReaderIndex() {
		this.readerIndex = markerIndex;
		this.token = nextToken();
	}

	@Override
	public void resetReaderIndex(int readerIndex) {
		this.token = null;
		this.readerIndex = readerIndex;
	}
}