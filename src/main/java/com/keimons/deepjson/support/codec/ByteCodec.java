package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

/**
 * {@link Byte}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteCodec extends BasePrimitiveCodec<Byte> {

	public static final ByteCodec instance = new ByteCodec();

	@Override
	protected void encode0(AbstractBuffer buf, Byte value) {
		buf.write(value);
	}

	@Override
	protected Byte decode0(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return Byte.valueOf(buf.stringValue());
	}
}