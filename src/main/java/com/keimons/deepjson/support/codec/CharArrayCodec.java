package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
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
public class CharArrayCodec extends AbstractArrayCodec<char[]> {

	public static final CharArrayCodec instance = new CharArrayCodec();

	@Override
	public void encode0(WriterContext context, JsonWriter writer, char[] values, long options) throws IOException {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				writer.writeMark(',');
			}
			writer.writeWithQuote(values[i]);
		}
	}

	@Override
	public char[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
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