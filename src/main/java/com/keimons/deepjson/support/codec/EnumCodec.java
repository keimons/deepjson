package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.lang.reflect.Type;

/**
 * {@link Enum}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EnumCodec extends BaseCodec<Enum<?>> {

	public static final EnumCodec instance = new EnumCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Enum<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.name());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Enum decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(stringExpects);
		String value = buf.stringValue();
		Class clazz = (Class<? extends Enum<?>>) type;
		return Enum.valueOf(clazz, value);
	}
}