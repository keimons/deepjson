package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderContext;

import java.io.IOException;

/**
 * {@link Byte}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteCodec extends AbstractPrimitiveCodec<Byte> {

	public static final ByteCodec instance = new ByteCodec();

	@Override
	protected void encode0(JsonWriter writer, Byte value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Byte decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(numberExpects, stringExpects);
		return reader.byteValue();
	}
}