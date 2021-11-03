package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

/**
 * {@link Byte}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteCodec extends AbstractPrimitiveCodec<Byte> {

	public static final ByteCodec instance = new ByteCodec();

	@Override
	protected void encode0(WriterBuffer buf, Byte value) {
		buf.write(value);
	}

	@Override
	protected Byte decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.byteValue();
	}
}