package com.keimons.deepjson.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public enum Wrapper {
	BOOLEAN(Boolean.class, boolean.class, 'Z', false),
	CHAR(Character.class, char.class, 'C', (char) 0),
	BYTE(Byte.class, byte.class, 'B', (byte) 0),
	SHORT(Short.class, short.class, 'S', (short) 0),
	INT(Integer.class, int.class, 'I', 0),
	LONG(Long.class, long.class, 'J', 0L),
	FLOAT(Float.class, float.class, 'F', 0F),
	DOUBLE(Double.class, double.class, 'D', 0D),
	VOID(Void.class, void.class, 'V', null),
	OBJECT(Object.class, Object.class, 'L', null),
	ARRAY(Object[].class, Object[].class, 'L', null);

	private final String name;
	private final Class<?> wType;
	private final Class<?> pType;
	private final char bType;
	private final Object defaultValue;

	Wrapper(Class<?> wType, Class<?> pType, char bType, Object defaultValue) {
		this.name = pType.getName();
		this.wType = wType;
		this.pType = pType;
		this.bType = bType;
		this.defaultValue = defaultValue;
	}

	public Class<?> getPType() {
		return pType;
	}

	private static Map<String, Wrapper> TYPES = new HashMap<>();

	static {
		for (Wrapper value : Wrapper.values()) {
			TYPES.put(value.name, value);
		}
	}

	public static Wrapper findWrapper(String name) {
		return TYPES.get(name);
	}
}