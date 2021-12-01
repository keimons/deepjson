package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

/**
 * {@code null}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NullCodec extends AbstractOnlineCodec<Void> {

	public static final ICodec<Void> instance = new NullCodec();

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Void value, int uniqueId, long options) {
		buf.writeNull();
	}

	@Override
	public Void decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(SyntaxToken.NULL);
		return null;
	}
}