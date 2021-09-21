package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;

import java.lang.reflect.Type;

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
	public void encode(AbstractContext context, AbstractBuffer buf, Float value, int uniqueId, long options) {
		buf.write(value);
	}

	@Override
	public Float decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.floatValue();
	}
}