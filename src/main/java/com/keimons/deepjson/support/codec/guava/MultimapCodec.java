package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.Multimap;
import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.AbstractClassCodec;

/**
 * Google Guava {@link Multimap}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MultimapCodec extends AbstractClassCodec<Multimap<?, ?>> {

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
	public Multimap<?, ?> decode(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Multimap<?, ?> value, int uniqueId, long options) {
		context.encode(buf, CodecModel.V, options);
	}
}