package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.JsonArray;

/**
 * {@link JsonArray}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonArrayCodec extends BaseCodec<JsonArray> {

	public static final JsonArrayCodec instance = new JsonArrayCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, JsonArray value, int uniqueId, long options) {

	}
}