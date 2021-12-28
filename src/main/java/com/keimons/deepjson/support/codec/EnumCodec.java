package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;

/**
 * {@link Enum}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EnumCodec extends KlassCodec<Enum<?>> {

	public static final EnumCodec instance = new EnumCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Enum<?> value, int uniqueId, long options) throws IOException {
		writer.writeWithQuote(value.name());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Enum decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(stringExpects);
		String value = reader.stringValue();
		return Enum.valueOf((Class) clazz, value);
	}
}