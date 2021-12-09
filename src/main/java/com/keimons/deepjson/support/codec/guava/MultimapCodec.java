package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.Multimap;
import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;

import java.io.IOException;

/**
 * Google Guava {@link Multimap}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MultimapCodec extends AbstractOnlineCodec<Multimap<?, ?>> {

	public static final MultimapCodec instance = new MultimapCodec();

	@Override
	public void build(WriterContext context, Multimap<?, ?> value) {
		context.build(value.asMap());
	}

	@Override
	public Multimap<?, ?> decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Multimap<?, ?> value, int uniqueId, long options) throws IOException {
		context.encode(writer, CodecModel.V, options);
	}
}