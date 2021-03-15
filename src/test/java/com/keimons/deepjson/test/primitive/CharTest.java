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
 * @since 1.7
 **/
public class CharTest {

	@Test
	public void test() {
		Gson instance = new Gson();
		System.out.println("---------------- value ----------------");
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

		System.out.println("---------------- field ----------------");
		CharNode fieldNode = new CharNode();
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			fieldNode.setValue(i);
			String fast = JSONObject.toJSONString(fieldNode).toLowerCase();
			String deep = DeepJson.toJsonString(fieldNode).toLowerCase();
			String gson = instance.toJson(fieldNode).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("char: " + (int) i);
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}

		System.out.println("---------------- array ----------------");
		ArrayCharNode arrayNode = new ArrayCharNode();
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			arrayNode.setValue(new char[]{0, 70, i, 8232, 8233, 8234});
			String deep = DeepJson.toJsonString(arrayNode).toLowerCase();
			String gson = instance.toJson(arrayNode).toLowerCase();
			if (deep.equals(gson)) {
				continue;
			}
			System.out.println("char: " + (int) i);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}
	}
}