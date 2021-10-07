package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

/**
 * {@link Character}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CharCodec extends BasePrimitiveCodec<Character> {

	public static final CharCodec instance = new CharCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Character value, int uniqueId, long options) {
		buf.writeWithQuote(value);
	}

	@Override
	protected void encode0(AbstractBuffer buf, Character value) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Character decode0(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(stringExpects);
		return buf.charValue();
	}
}