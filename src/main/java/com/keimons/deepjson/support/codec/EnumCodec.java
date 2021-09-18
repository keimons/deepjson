package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

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
	public void encode(AbstractContext context, AbstractBuffer buf, Enum<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.name());
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(stringExpects);
		String value = buf.stringValue();
		Class clazz = (Class<? extends Enum<?>>) type;
		return Enum.valueOf(clazz, value);
	}
}