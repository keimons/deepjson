package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

/**
 * {@link Integer}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IntegerCodec extends BasePrimitiveCodec<Integer> {

	public static final IntegerCodec instance = new IntegerCodec();

	@Override
	protected void encode0(WriterBuffer buf, Integer value) {
		buf.write(value);
	}

	@Override
	protected Integer decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.intValue();
	}
}