package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

/**
 * {@link String}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringCodec extends AbstractRawCodec<String> {

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
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, String value, int uniqueId, long options) {
		buf.writeWithQuote(value);
	}

	public String decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		if (buf.token() == SyntaxToken.NULL) {
			return null;
		}
		buf.assertExpectedSyntax(SyntaxToken.STRING);
		return buf.stringValue(); // successful
	}
}