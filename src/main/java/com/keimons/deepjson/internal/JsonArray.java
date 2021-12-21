package com.keimons.deepjson.internal;

import java.util.Iterator;

/**
 * 默认{@code json}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface JsonArray extends Iterable<Object> {

	void add(Object value);

	int size();

	Object get(int index);

	boolean getBooleanValue(int index);

	Boolean getBoolean(int index);

	char getCharValue(int index);

	Character getCharacter(int index);

	byte getByteValue(int index);

	Byte getByte(int index);

	short getShortValue(int index);

	Short getShort(int index);

	int getIntValue(int index);

	Integer getInteger(int index);

	long getLongValue(int index);

	Long getLong(int index);

	float getFloatValue(int index);

	Float getFloat(int index);

	double getDoubleValue(int index);

	Double getDouble(int index);

	Iterator<Object> iterator();

	Object remove(int index);

	boolean remove(Object value);

	void clear();

	boolean equals(Object obj);
}