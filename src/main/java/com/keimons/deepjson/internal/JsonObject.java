package com.keimons.deepjson.internal;

import java.util.Map;
import java.util.Set;

/**
 * 默认{@code json}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface JsonObject {

	void put(String key, Object value);

	int size();

	Object get(String key);

	boolean getBooleanValue(String key);

	Boolean getBoolean(String key);

	char getCharValue(String key);

	Character getCharacter(String key);

	byte getByteValue(String key);

	Byte getByte(String key);

	short getShortValue(String key);

	Short getShort(String key);

	int getIntValue(String key);

	Integer getInteger(String key);

	long getLongValue(String key);

	Long getLong(String key);

	float getFloatValue(String key);

	Float getFloat(String key);

	double getDoubleValue(String key);

	Double getDouble(String key);

	Set<Map.Entry<String, Object>> entrySet();

	void clear();

	boolean equals(Object obj);
}