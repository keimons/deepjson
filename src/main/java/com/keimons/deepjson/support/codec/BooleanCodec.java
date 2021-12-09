package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;

import java.io.IOException;

/**
 * {@link Boolean}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BooleanCodec extends AbstractPrimitiveCodec<Boolean> {

	public static final BooleanCodec instance = new BooleanCodec();

	@Override
	protected void encode0(JsonWriter writer, Boolean value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Boolean decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(SyntaxToken.TRUE, SyntaxToken.FALSE, SyntaxToken.STRING);
		return buf.booleanValue();
	}
}