package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

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
	protected void encode0(AbstractBuffer buf, Double value) {
		buf.write(value);
	}

	@Override
	protected Double decode0(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.doubleValue();
	}
}