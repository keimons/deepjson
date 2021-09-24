package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;

/**
 * {@link String}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringCodec extends BaseCodec<String> {

	public static final StringCodec instance = new StringCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, String value, int uniqueId, long options) {
		buf.writeWithQuote(value);
	}

	public String decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		if (buf.token() == SyntaxToken.NULL) {
			return null;
		}
		buf.assertExpectedSyntax(SyntaxToken.STRING);
		return buf.stringValue(); // successful
	}
}