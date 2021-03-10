package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@link Enum}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class EnumSerializer implements ISerializer {

	public static final EnumSerializer instance = new EnumSerializer();

	@Override
	public int length(Object object, long options) {
		Enum<?> value = (Enum<?>) object;
		return value.name().length() + 2;
	}

	@Override
	public byte coder(Object object, long options) {
		Enum<?> value = (Enum<?>) object;
		return SerializerUtil.coder(value.name());
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		Enum<?> value = (Enum<?>) object;
		buf.writeString(value.name());
	}
}