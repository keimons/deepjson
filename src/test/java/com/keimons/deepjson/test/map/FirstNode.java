package com.keimons.deepjson.test.map;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FirstNode<S> extends HashMap<S, Integer> implements Function<String, Object> {


	@Override
	public Object apply(String s) {
		return null;
	}
}