package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderContext;

import java.io.IOException;

/**
 * {@link Short}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ShortCodec extends AbstractPrimitiveCodec<Short> {

	public static final ShortCodec instance = new ShortCodec();

	@Override
	protected void encode0(JsonWriter writer, Short value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Short decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(numberExpects, stringExpects);
		return reader.shortValue();
	}
}