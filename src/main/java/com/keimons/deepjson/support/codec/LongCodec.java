package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

/**
 * {@link Long}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class LongCodec extends BasePrimitiveCodec<Long> {

	public static final LongCodec instance = new LongCodec();

	@Override
	protected void encode0(AbstractBuffer buf, Long value) {
		buf.write(value);
	}

	@Override
	protected Long decode0(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.longValue();
	}
}