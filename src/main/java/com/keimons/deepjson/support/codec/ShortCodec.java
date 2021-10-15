package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

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
	protected void encode0(WriterBuffer buf, Short value) {
		buf.write(value);
	}

	@Override
	protected Short decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.shortValue();
	}
}