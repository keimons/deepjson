package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.WriterBuffer;

/**
 * {@link Float}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FloatCodec extends BasePrimitiveCodec<Float> {

	public static final FloatCodec instance = new FloatCodec();

	@Override
	protected void encode0(WriterBuffer buf, Float value) {
		buf.write(value);
	}

	@Override
	protected Float decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.floatValue();
	}
}