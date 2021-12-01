package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.internal.BridgeUtil;
import com.keimons.deepjson.util.ReflectUtil;

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

	public static final Type TYPE_LIST = ReflectUtil.makeParameterizedType(null, List.class,
			ReflectUtil.makeTypeVariable(Map.class, "E", new Type[]{Object.class})
	);
	public static final Type TYPE_DICT = ReflectUtil.makeParameterizedType(null, Map.class,
			ReflectUtil.makeTypeVariable(Map.class, "K", new Type[]{String.class}),
			ReflectUtil.makeTypeVariable(Map.class, "V", new Type[]{Object.class})
	);

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
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Json value, int uniqueId, long options) {
		Object v = context.poll();
		if (!(v instanceof Map || v instanceof List)) {
			throw new RuntimeException("deep json bug");
		}
		if (v instanceof Map) {
			MapCodec.instance.encode(context, buf, model, v, uniqueId, options);
		} else {
			CollectionCodec.instance.encode(context, buf, model, (List<?>) v, uniqueId, options);
		}
	}

	@Override
	protected Json decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		if (buf.token() == SyntaxToken.LBRACE) {
			Object values = context.decode(buf, TYPE_DICT, options);
			if (values instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> dict = (Map<String, Object>) values;
				return new Json(dict);
			} else if (values instanceof List) {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) values;
				return new Json(list);
			} else {
				throw new RuntimeException();
			}
		} else if (buf.token() == SyntaxToken.LBRACKET) {
			List<Object> values = context.decode(buf, TYPE_LIST, options);
			return new Json(values);
		}
		throw new RuntimeException();
	}
}