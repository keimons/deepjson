package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

/**
 * {@link Enum}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EnumCodec extends AbstractClassCodec<Enum<?>> {

	public static final EnumCodec instance = new EnumCodec();

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Enum<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.name());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Enum decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(stringExpects);
		String value = buf.stringValue();
		return Enum.valueOf((Class) clazz, value);
	}
}