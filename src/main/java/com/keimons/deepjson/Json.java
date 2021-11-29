package com.keimons.deepjson;

import com.keimons.deepjson.internal.AbstractJson;

import java.util.List;
import java.util.Map;

/**
 * {@code JsonObject}和{@code JsonArray}的集合实现
 * <p>
 * {@code DeepJson}中不区分{@code map}和{@code list}，采用同一个实现。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see com.keimons.deepjson.internal.JsonArray {@code list}实现
 * @see com.keimons.deepjson.internal.JsonObject {@code map}实现
 * @since 1.6
 **/
public class Json extends AbstractJson {

	public Json() {

	}

	public Json(List<Object> values) {
		super(values);
	}

	public Json(Map<String, Object> values) {
		super(values);
	}
}