package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.SerializerOptions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link java.util.Map}序列化测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class MapTest {

	@Test
	public void test() {
		Map<MapKeyNode, String> map = new HashMap<>();
		map.put(new MapKeyNode(), "1000");
		System.out.println(DeepJson.toJsonString(map, SerializerOptions.ForceClassName));
		System.out.println(JSONObject.toJSONString(map));
		System.out.println(new Gson().toJson(map));
	}
}