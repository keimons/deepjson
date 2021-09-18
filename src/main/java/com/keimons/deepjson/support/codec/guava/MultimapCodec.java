package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.Multimap;
import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.support.codec.BaseCodec;

/**
 * Google Guava {@link Multimap}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MultimapCodec extends BaseCodec<Multimap<?, ?>> {

	public static final MultimapCodec instance = new MultimapCodec();

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public void build(AbstractContext context, Multimap<?, ?> value) {
		context.build(value.asMap());
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Multimap<?, ?> value, int uniqueId, long options) {
		context.encode(buf, options);
	}
}