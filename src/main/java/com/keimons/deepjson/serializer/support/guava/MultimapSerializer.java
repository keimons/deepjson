package com.keimons.deepjson.serializer.support.guava;

import com.google.common.collect.Multimap;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.MapSerializer;

import java.util.Collection;
import java.util.Map;

/**
 * Google Guava {@link Multimap}序列化
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class MultimapSerializer implements ISerializer {

	public static final MultimapSerializer instance = new MultimapSerializer();

	@Override
	public int length(Object object, long options) {
		Map<?, ? extends Collection<?>> value = ((Multimap<?, ?>) object).asMap();
		return MapSerializer.instance.length(value, options);
	}

	@Override
	public byte coder(Object object, long options) {
		Map<?, ? extends Collection<?>> value = ((Multimap<?, ?>) object).asMap();
		return MapSerializer.instance.coder(value, options);
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		Map<?, ? extends Collection<?>> value = ((Multimap<?, ?>) object).asMap();
		MapSerializer.instance.write(value, options, buf);
	}
}