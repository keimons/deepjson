package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
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
public class ShortArrayCodec extends AbstractArrayCodec<short[]> {

	public static final ShortArrayCodec instance = new ShortArrayCodec();

	@Override
	public void encode0(WriterContext context, JsonWriter writer, short[] values, long options) throws IOException {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				writer.writeMark(',');
			}
			writer.write(values[i]);
		}
	}

	@Override
	public short[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
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