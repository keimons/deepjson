package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

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
	protected void encode0(WriterBuffer buf, Long value) {
		buf.write(value);
	}

	@Override
	protected Long decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.longValue();
	}
}