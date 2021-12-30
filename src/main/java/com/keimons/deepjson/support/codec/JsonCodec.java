package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.internal.BridgeUtil;
import com.keimons.deepjson.util.TypeUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 融合{@code json}编码方案，不区分数组或者映射。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonCodec extends AbstractOnlineCodec<Json> {

	public static final JsonCodec instance = new JsonCodec();

	public static final Type TYPE_LIST = TypeUtil.makeType(List.class, Object.class);
	public static final Type TYPE_DICT = TypeUtil.makeType(Map.class, String.class, Object.class);

	@Override
	public void build(WriterContext context, Json value) {
		Object values = BridgeUtil.getValues(value);
		if (values instanceof Map) {
			context.cache(values, MapCodec.instance);
			MapCodec.instance.build(context, values);
		} else if (values instanceof List) {
			List<?> list = (List<?>) values;
			context.cache(list, CollectionCodec.instance);
			CollectionCodec.instance.build(context, list);
		}
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Json value, int uniqueId, long options) throws IOException {
		Object v = context.poll();
		if (!(v instanceof Map || v instanceof List)) {
			throw new RuntimeException("deep json bug");
		}
		if (v instanceof Map) {
			MapCodec.instance.encode(context, writer, model, v, uniqueId, options);
		} else {
			CollectionCodec.instance.encode(context, writer, model, (List<?>) v, uniqueId, options);
		}
	}

	@Override
	protected Json decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		if (reader.token() == SyntaxToken.LBRACE) {
			Object values = context.decode(reader, TYPE_DICT, options);
			if (values instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dict = (Map<String, Object>) values;
				Json json = new Json();
				BridgeUtil.putValues(json, dict);
				return json;
			} else if (values instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) values;
				Json json = new Json();
				BridgeUtil.putValues(json, list);
				return json;
			} else {
				throw new RuntimeException();
			}
		} else if (reader.token() == SyntaxToken.LBRACKET) {
			List<Object> values = context.decode(reader, TYPE_LIST, options);
			Json json = new Json();
			BridgeUtil.putValues(json, values);
			return json;
		} else if (reader.token() == SyntaxToken.NULL) {
			return null;
		}
		throw new RuntimeException();
	}
}