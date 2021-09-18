package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code short[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ShortArrayCodec extends BaseArrayCodec<short[]> {

	public static final ShortArrayCodec instance = new ShortArrayCodec();

	@Override
	public void encode0(short[] values, AbstractBuffer buf, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.write(values[i]);
		}
	}

	@Override
	public Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		List<Short> values = new ArrayList<Short>();
		for (; ; ) {
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(Short.valueOf(buf.stringValue()));
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		short[] result = new short[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}