package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;

/**
 * {@link Class}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassCodec extends BaseCodec<Class<?>> {

	public static final ClassCodec instance = new ClassCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Class<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.getName());
	}
}