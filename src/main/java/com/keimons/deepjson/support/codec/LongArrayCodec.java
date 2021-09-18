package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code long[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class LongArrayCodec extends BaseArrayCodec<long[]> {

	public static final LongArrayCodec instance = new LongArrayCodec();

	@Override
	public void encode0(long[] values, AbstractBuffer buf, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.write(values[i]);
		}
	}

	@Override
	public Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		List<Long> values = new ArrayList<Long>();
		for (; ; ) {
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(buf.longValue());
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		long[] result = new long[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}