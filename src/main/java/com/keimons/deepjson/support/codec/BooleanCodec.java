package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;

/**
 * {@link Boolean}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BooleanCodec extends BasePrimitiveCodec<Boolean> {

	public static final BooleanCodec instance = new BooleanCodec();

	@Override
	protected void encode0(AbstractBuffer buf, Boolean value) {
		buf.write(value);
	}

	@Override
	protected Boolean decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(SyntaxToken.TRUE, SyntaxToken.FALSE, SyntaxToken.STRING);
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.TRUE) {
			return Boolean.TRUE;
		}
		if (token == SyntaxToken.FALSE) {
			return Boolean.FALSE;
		}
		return Boolean.parseBoolean(buf.stringValue());
	}
}