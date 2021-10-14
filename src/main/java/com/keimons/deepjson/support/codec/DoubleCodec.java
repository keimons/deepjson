package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

/**
 * {@link Double}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DoubleCodec extends BasePrimitiveCodec<Double> {

	public static final DoubleCodec instance = new DoubleCodec();

	@Override
	protected void encode0(WriterBuffer buf, Double value) {
		buf.write(value);
	}

	@Override
	protected Double decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.doubleValue();
	}
}