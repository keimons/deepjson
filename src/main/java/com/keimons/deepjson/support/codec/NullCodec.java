package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;

/**
 * {@code null}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NullCodec extends BaseCodec<Void> {

	public static final ICodec<Void> instance = new NullCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Void value, int uniqueId, long options) {
		buf.writeNull();
	}

	@Override
	public Void decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(SyntaxToken.NULL);
		return null;
	}
}