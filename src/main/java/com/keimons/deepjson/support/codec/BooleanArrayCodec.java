package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code boolean[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BooleanArrayCodec extends AbstractArrayCodec<boolean[]> {

	public static final BooleanArrayCodec instance = new BooleanArrayCodec();

	@Override
	public void encode0(WriterContext context, JsonWriter writer, boolean[] values, long options) throws IOException {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				writer.writeMark(',');
			}
			writer.write(values[i]);
		}
	}

	@Override
	public boolean[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
		List<Boolean> values = new ArrayList<Boolean>();
		SyntaxToken token;
		for (; ; ) {
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.TRUE, SyntaxToken.FALSE, SyntaxToken.STRING);
			if (token == SyntaxToken.TRUE) {
				values.add(Boolean.TRUE);
			}
			if (token == SyntaxToken.FALSE) {
				values.add(Boolean.FALSE);
			}
			if (token == SyntaxToken.STRING) {
				values.add(buf.booleanValue());
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		boolean[] result = new boolean[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}