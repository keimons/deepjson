package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;

/**
 * {@link Character}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharCodec extends AbstractPrimitiveCodec<Character> {

	public static final CharCodec instance = new CharCodec();

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Character value, int uniqueId, long options) throws IOException {
		writer.writeWithQuote(value);
	}

	@Override
	protected void encode0(JsonWriter writer, Character value) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Character decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(stringExpects);
		return reader.charValue();
	}
}