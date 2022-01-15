package com.keimons.deepjson.internal.util;

import java.lang.invoke.MethodHandle;
import java.util.Map;

/**
 * Hook
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Hook {

	Map<Object, Object> values;

	String property;

	int id;

	MethodHandle setter;

	Object instance;
}