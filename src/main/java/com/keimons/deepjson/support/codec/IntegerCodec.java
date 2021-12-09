package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;

import java.io.IOException;

/**
 * {@link Integer}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class IntegerCodec extends AbstractPrimitiveCodec<Integer> {

	public static final IntegerCodec instance = new IntegerCodec();

	@Override
	protected void encode0(JsonWriter writer, Integer value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Integer decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.intValue();
	}
}