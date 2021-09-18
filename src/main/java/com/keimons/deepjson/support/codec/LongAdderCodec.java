package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;

import java.util.concurrent.atomic.LongAdder;

/**
 * {@link LongAdder}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class LongAdderCodec extends BaseCodec<LongAdder> {

	public static final LongAdderCodec instance = new LongAdderCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, LongAdder value, int uniqueId, long options) {
		char mark = '{';
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		buf.writeValue(mark, FIELD_VALUE, value.sum());
		buf.writeMark('}');
	}
}