package com.keimons.deepjson.filler;

import com.keimons.deepjson.ISerializer;

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
	public byte coder(Object object) {
		return serializer.coder(object);
	}

	@Override
	public int length(Object object) {
		try {
			return serializer.size(handle.invoke(object));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex) {
		try {
			return serializer.concat(handle.invoke(object), code, coder, writeIndex);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return writeIndex;
	}
}