package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.RangeMap;
import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;

/**
 * Google Guava {@link RangeMap}编解码器 (暂不支持)
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
@Deprecated
public class RangeMapCodec extends AbstractOnlineCodec<RangeMap> {

	@Override
	public void build(WriterContext context, RangeMap value) {

	}

	@Override
	public RangeMap decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		return null;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, RangeMap value, int uniqueId, long options) {

	}
}