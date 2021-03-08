package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

/**
 * 数组序列化方案
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ArraySerializer implements ISerializer {

	private Class<?> componentType;

	private ISerializer serializer;

	public ArraySerializer(Class<?> clazz) {
		componentType = clazz.getComponentType();
	}

	public void link() {
		serializer = SerializerFactory.getSerializer(componentType);
	}

	@Override
	public int length(Object object, long options) {
		int length = 2;
		Object[] array = (Object[]) object;
		for (Object item : array) {
			if (item == null) {
				length += 4;
			} else {
				ISerializer serializer = SerializerFactory.getSerializer(item.getClass());
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
		for (Object item : array) {
			if (item == null) {
				continue;
			}
			ISerializer serializer = SerializerFactory.getSerializer(item.getClass());
			if (serializer.coder(item, options) == 1) {
				return 1;
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
			for (int i = 0; i < array.length; i++) {
				if (i != 0) {
					buf.writeMark(',');
				}
				Object item = array[i];
				if (item == null) {
					buf.writeNull();
					continue;
				}
				if (item.getClass() == componentType) {
					serializer.write(item, buf);
				} else {
					ISerializer serializer = SerializerFactory.getSerializer(item.getClass());
					serializer.write(item, buf);
				}
			}
			buf.writeMark(']');
		}
	}
}