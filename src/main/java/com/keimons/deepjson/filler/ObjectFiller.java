package com.keimons.deepjson.filler;

import com.keimons.deepjson.serializer.ISerializer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class ObjectFiller implements IFiller {

	private MethodHandle handle;

	private ISerializer serializer;

	public ObjectFiller(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
		Class<?> type = field.getType();
		handle = lookup.findGetter(clazz, field.getName(), type);
	}

	@Override
	public byte coder(Object object, long options) {
		return serializer.coder(object, options);
	}

	@Override
	public int length(Object object, long options) {
		try {
			return serializer.length(handle.invoke(object), options);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex, long options) {
		try {
			return serializer.write(handle.invoke(object), code, coder, writeIndex, options);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return writeIndex;
	}
}