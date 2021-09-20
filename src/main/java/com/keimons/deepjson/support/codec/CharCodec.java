package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

import java.lang.reflect.Type;

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
	public void encode(AbstractContext context, AbstractBuffer buf, Character value, int uniqueId, long options) {
		buf.writeWithQuote(value);
	}

	@Override
	public void writeKey(AbstractContext context, AbstractBuffer buf, Character value, int uniqueId, long options) {
		// char don't need double quote.
		encode(context, buf, value, uniqueId, options);
	}

	@Override
	public Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(stringExpects);
		return buf.charValue();
	}
}