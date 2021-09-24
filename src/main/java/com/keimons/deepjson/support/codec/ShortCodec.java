package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

import java.lang.reflect.Type;

/**
 * {@link Short}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ShortCodec extends BasePrimitiveCodec<Short> {

	public static final ShortCodec instance = new ShortCodec();

	@Override
	protected void encode0(AbstractBuffer buf, Short value) {
		buf.write(value);
	}

	@Override
	protected Short decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return Short.valueOf(buf.stringValue());
	}
}