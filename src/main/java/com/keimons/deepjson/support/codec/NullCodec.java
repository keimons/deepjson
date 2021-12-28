package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;

/**
 * {@code null}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NullCodec extends KlassCodec<Void> {

	public static final ICodec<Void> instance = new NullCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Void value, int uniqueId, long options) throws IOException {
		writer.writeNull();
	}

	@Override
	public Void decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(SyntaxToken.NULL);
		return null;
	}
}