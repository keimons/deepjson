package com.keimons.deepjson;

import com.keimons.deepjson.internal.AbstractJson;

/**
 * 默认{@code json}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface JsonObject {

	Object get(String key);

	static JsonObject create() {
		return new AbstractJson();
	}
}