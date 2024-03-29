package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;

/**
 * {@link String}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringCodec extends KlassCodec<String> {

	public static final StringCodec instance = new StringCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, String value, int uniqueId, long options) throws IOException {
		writer.writeWithQuote(value);
	}

	public String decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		if (reader.token() == SyntaxToken.NULL) {
			return null;
		}
		reader.assertExpectedSyntax(SyntaxToken.STRING);
		return reader.stringValue(); // successful
	}
}