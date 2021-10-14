package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;
import com.keimons.deepjson.WriterContext;
import com.keimons.deepjson.support.SyntaxToken;

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
public class BooleanArrayCodec extends BaseArrayCodec<boolean[]> {

	public static final BooleanArrayCodec instance = new BooleanArrayCodec();

	@Override
	public void encode0(WriterContext context, WriterBuffer buf, boolean[] values, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.write(values[i]);
		}
	}

	@Override
	public boolean[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
		List<Boolean> values = new ArrayList<Boolean>();
		for (; ; ) {
			SyntaxToken token = buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.TRUE, SyntaxToken.FALSE, SyntaxToken.STRING);
			if (token == SyntaxToken.TRUE) {
				values.add(Boolean.TRUE);
			}
			if (token == SyntaxToken.FALSE) {
				values.add(Boolean.FALSE);
			}
			if (token == SyntaxToken.STRING) {
				values.add(Boolean.valueOf(buf.stringValue()));
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