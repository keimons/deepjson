package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

/**
 * {@link Object[]}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ObjectArraySerializer implements ISerializer {

	public static final ObjectArraySerializer instance = new ObjectArraySerializer();

	@Override
	public int length(Object object, long options) {
		int length = 2;
		Class<?> cache = null; // 缓存数组对象类型
		ISerializer serializer = null;
		Object[] array = (Object[]) object;
		for (Object item : array) {
			if (item == null) {
				length += 4;
			} else {
				if (item.getClass() != cache) {
					cache = item.getClass();
					serializer = SerializerFactory.getSerializer(item.getClass());
				}
				length += serializer.length(item, options);
			}
		}
		if (array.length > 0) {
			length += array.length - 1;
		}
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		Object[] array = (Object[]) object;
		Class<?> cache = null; // 缓存数组对象类型
		ISerializer serializer = null;
		for (Object item : array) {
			if (item != null) {
				Class<?> clazz = item.getClass();
				if (clazz != cache) {
					cache = clazz;
					serializer = SerializerFactory.getSerializer(item.getClass());
				}
				if (serializer.coder(item, options) == 1) {
					return 1;
				}
			}
		}
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeMark('[');
			Object[] array = (Object[]) object;
			Class<?> cache = null; // 缓存数组对象类型
			ISerializer serializer = null;
			for (int i = 0; i < array.length; i++) {
				if (i != 0) {
					buf.writeMark(',');
				}
				Object item = array[i];
				if (item == null) {
					buf.writeNull();
				} else {
					Class<?> clazz = item.getClass();
					if (clazz != cache) {
						cache = clazz;
						serializer = SerializerFactory.getSerializer(cache);
					}
					serializer.write(item, buf);
				}
			}
			buf.writeMark(']');
		}
	}
}