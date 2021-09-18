package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;

import java.util.concurrent.atomic.DoubleAdder;

/**
 * {@link DoubleAdder}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class DoubleAdderCodec extends BaseCodec<DoubleAdder> {

	public static final DoubleAdderCodec instance = new DoubleAdderCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, DoubleAdder value, int uniqueId, long options) {
		char mark = '{';
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		buf.writeValue(mark, FIELD_VALUE, value.sum());
		buf.writeMark('}');
	}
}