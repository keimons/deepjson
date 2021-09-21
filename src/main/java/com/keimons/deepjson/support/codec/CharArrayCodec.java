package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code char[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharArrayCodec extends BaseArrayCodec<char[]> {

	public static final CharArrayCodec instance = new CharArrayCodec();

	@Override
	public void encode0(char[] values, AbstractBuffer buf, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.writeWithQuote(values[i]);
		}
	}

	@Override
	public char[] decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		List<Character> values = new ArrayList<Character>();
		for (; ; ) {
			buf.nextToken();
			buf.assertExpectedSyntax(stringExpects);
			values.add(buf.charValue());
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		char[] result = new char[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}