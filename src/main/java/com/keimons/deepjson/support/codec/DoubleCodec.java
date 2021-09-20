package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

import java.lang.reflect.Type;

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
	public void encode(AbstractContext context, AbstractBuffer buf, Double value, int uniqueId, long options) {
		buf.write(value);
	}

	@Override
	public Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.doubleValue();
	}
}