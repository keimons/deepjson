package com.keimons.deepjson.test.map;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashMap;

/**
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EnumMapTest {

	public EnumMap<EnumNode, String> map = new EnumMap<EnumNode, String>(EnumNode.class);

	@Test
	@SuppressWarnings("unchecked")
	public void test() throws NoSuchFieldException {
		Field field = EnumMapTest.class.getField("map");
		EnumNode node = DeepJson.parseObject("\"TEST\"", EnumNode.class);
		System.out.println(node);
		HashMap<Object, Object> map = DeepJson.parseObject("{\"$type\":\"java.util.HashMap\",\"TEST\":\"successful\"}", HashMap.class);
		System.out.println(map.get(node.name()));
	}
}