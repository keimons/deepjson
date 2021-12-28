package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code float[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FloatArrayCodec extends AbstractArrayCodec<float[]> {

	public static final FloatArrayCodec instance = new FloatArrayCodec();

	@Override
	public void encode0(WriterContext context, JsonWriter writer, float[] values, long options) throws IOException {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				writer.writeMark(',');
			}
			writer.write(values[i]);
		}
	}

	@Override
	public float[] decode0(ReaderContext context, JsonReader reader, Class<?> instanceType, Type componentType, long options) {
		List<Float> values = new ArrayList<Float>();
		SyntaxToken token;
		for (; ; ) {
			token = reader.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			reader.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(reader.floatValue());
			token = reader.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			reader.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		float[] result = new float[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}