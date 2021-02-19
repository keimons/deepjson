package com.keimons.deepjson.filler;

import java.lang.reflect.Field;

public class FillerFactory {

	public static IFiller create(Class<?> clazz, Field field) throws NoSuchFieldException, IllegalAccessException {
		Class<?> type = field.getType();
		if (type == byte.class || type == Byte.class) {
			return new ByteFiller(clazz, field);
		} else if (type == short.class || type == Short.class) {
			return new ShortFiller(clazz, field);
		} else if (type == int.class || type == Integer.class) {
			return new IntegerFiller(clazz, field);
		} else if (type == long.class || type == Long.class) {
			return new LongFiller(clazz, field);
		} else if (type == boolean.class || type == Boolean.class) {
			return new BooleanFiller(clazz, field);
		} else if (type == char.class || type == Character.class) {
			return new CharFiller(clazz, field);
		} else if (type == float.class || type == Float.class) {
			return new FloatFiller(clazz, field);
		} else if (type == double.class || type == Double.class) {
			return new FloatFiller(clazz, field);
		} else {
			return new ObjectFiller(clazz, field);
		}
	}
}