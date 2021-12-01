package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code byte[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteArrayCodec extends AbstractArrayCodec<byte[]> {

	public static final ByteArrayCodec instance = new ByteArrayCodec();

	@Override
	public void encode0(WriterContext context, WriterBuffer buf, byte[] values, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.write(values[i]);
		}
	}

	@Override
	public byte[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
		List<Byte> values = new ArrayList<Byte>();
		for (; ; ) {
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(Byte.valueOf(buf.stringValue()));
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		byte[] result = new byte[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}