package com.keimons.deepjson.filler;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.serializer.ByteBuf;
import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.SerializerFactory;

import java.lang.reflect.Field;

public class ObjectFiller extends BaseFiller {

	public ObjectFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public byte coder(Object object, long options) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			return 0;
		}
		ISerializer writer = SerializerFactory.getWriter(value.getClass());
		return (byte) (coder | writer.coder(value, options));
	}

	@Override
	public int length(Object object, long options) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			if (SerializerOptions.IgnoreNonField.isOptions(options)) {
				return size + 4;
			} else {
				return 0;
			}
		}
		ISerializer writer = SerializerFactory.getWriter(value.getClass());
		return size + writer.length(value, options);
	}

	@Override
	public int concat(Object object, ByteBuf buf) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			return buf.writeNull();
		} else {
			return buf.writeObject(this, value);
		}
	}
}