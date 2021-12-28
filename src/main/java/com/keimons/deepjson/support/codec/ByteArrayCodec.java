package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
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
	public void encode0(WriterContext context, JsonWriter writer, byte[] values, long options) throws IOException {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				writer.writeMark(',');
			}
			writer.write(values[i]);
		}
	}

	@Override
	public byte[] decode0(ReaderContext context, JsonReader reader, Class<?> instanceType, Type componentType, long options) {
		List<Byte> values = new ArrayList<Byte>();
		SyntaxToken token;
		for (; ; ) {
			token = reader.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			reader.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(reader.byteValue());
			token = reader.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			reader.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		byte[] result = new byte[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}