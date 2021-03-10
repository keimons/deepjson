package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

/**
 * {@link Class}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ClassSerializer implements ISerializer {

	public static final ClassSerializer instance = new ClassSerializer();

	@Override
	public int length(Object object, long options) {
		Class<?> clazz = (Class<?>) object;
		return clazz.getName().length() + 2;
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		Class<?> clazz = (Class<?>) object;
		buf.writeString(clazz.getName());
	}
}