package com.keimons.deepjson.serializer.support.guava;

import com.google.common.collect.Table;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.MapSerializer;

import java.util.Map;

/**
 * Google Guava {@link Table}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class TableSerializer implements ISerializer {

	public static final TableSerializer instance = new TableSerializer();

	@Override
	public int length(Object object, long options) {
		Map<?, ? extends Map<?, ?>> value = ((Table<?, ?, ?>) object).columnMap();
		return MapSerializer.instance.length(value, options);
	}

	@Override
	public byte coder(Object object, long options) {
		Map<?, ? extends Map<?, ?>> value = ((Table<?, ?, ?>) object).columnMap();
		return MapSerializer.instance.coder(value, options);
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		Map<?, ? extends Map<?, ?>> value = ((Table<?, ?, ?>) object).columnMap();
		MapSerializer.instance.write(value, options, buf);
	}
}