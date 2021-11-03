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
 * {@code float[]}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FloatArrayCodec extends AbstractArrayCodec<float[]> {

	public static final FloatArrayCodec instance = new FloatArrayCodec();

	@Override
	public void encode0(WriterContext context, WriterBuffer buf, float[] values, long options) {
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			buf.write(values[i]);
		}
	}

	@Override
	public float[] decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options) {
		List<Float> values = new ArrayList<Float>();
		for (; ; ) {
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			values.add(buf.floatValue());
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		float[] result = new float[values.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = values.get(i);
		}
		return result;
	}
}