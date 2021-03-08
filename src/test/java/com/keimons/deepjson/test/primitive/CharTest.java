package com.keimons.deepjson.test.primitive;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * char测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class CharTest {

	@Test
	public void test() {
		Gson instance = new Gson();
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			String fast = JSONObject.toJSONString(i).toLowerCase();
			String deep = DeepJson.toJsonString(i).toLowerCase();
			String gson = instance.toJson(i).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("char: " + (int) i);
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}
	}
}