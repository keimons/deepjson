package com.keimons.deepjson.test.map;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MapCodecTest {

	public Map<Integer, Object> field = new HashMap<Integer, Object>();
	public Map<Integer, HashMap<String, Long>> node;

	@Test
	@SuppressWarnings("unchecked")
	public void test() throws NoSuchFieldException {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(map, map);
		String json = DeepJson.toJsonString(map);
		System.out.println(json);
		Map<Object, Object> parse = DeepJson.parseObject(json, Map.class);
		// ! do not get(parse);
		System.out.println(DeepJson.toJsonString(parse));
		for (Object obj : parse.keySet()) {
			System.out.println(parse == obj);
		}
		for (Object obj : parse.values()) {
			System.out.println(parse == obj);
		}
	}
}