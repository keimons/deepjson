package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;

import java.io.IOException;

/**
 * {@link Long}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class LongCodec extends AbstractPrimitiveCodec<Long> {

	public static final LongCodec instance = new LongCodec();

	@Override
	protected void encode0(JsonWriter writer, Long value) throws IOException {
		writer.write(value);
	}

	@Override
	protected Long decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(numberExpects, stringExpects);
		return buf.longValue();
	}
}