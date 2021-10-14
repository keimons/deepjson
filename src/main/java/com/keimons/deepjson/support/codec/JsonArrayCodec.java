package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

/**
 * {@link JsonArray}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonArrayCodec extends AbstractClassCodec<JsonArray> {

	public static final JsonArrayCodec instance = new JsonArrayCodec();

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, JsonArray value, int uniqueId, long options) {

	}

	@Override
	public JsonArray decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}
}