package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderContext;

import java.io.IOException;

/**
 * {@link Double}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DoubleCodec extends AbstractPrimitiveCodec<Double> {

	public static final DoubleCodec instance = new DoubleCodec();

	@Override
	protected void encode0(JsonWriter writer, Double value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Double decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(numberExpects, stringExpects);
		return reader.doubleValue();
	}
}