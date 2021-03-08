package com.keimons.deepjson.test.string;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * String类型测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class StringTest {

	@Test
	public void test() {
		Gson instance = new Gson();
		String s1 = "deep ";
		String s2 = " json";
		System.out.println("---------------- value ----------------");
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			String value = s1 + i + s2;
			String fast = JSONObject.toJSONString(value).toLowerCase();
			String deep = DeepJson.toJsonString(value).toLowerCase();
			String gson = instance.toJson(value).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("value: " + value);
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}

		s1 = "蒙奇 ";
		s2 = " 开发";
		System.out.println("---------------- value ----------------");
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			String value = s1 + i + s2;
			String fast = JSONObject.toJSONString(value).toLowerCase();
			String deep = DeepJson.toJsonString(value).toLowerCase();
			String gson = instance.toJson(value).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("value: " + value);
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}

		System.out.println("---------------- field ----------------");
		StringNode fieldNode = new StringNode();
		s1 = "deep ";
		s2 = " json";
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			fieldNode.setValue(s1 + i + s2);
			String fast = JSONObject.toJSONString(fieldNode).toLowerCase();
			String deep = DeepJson.toJsonString(fieldNode).toLowerCase();
			String gson = instance.toJson(fieldNode).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("value: " + fieldNode.getValue());
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}

		s1 = "蒙奇 ";
		s2 = " 开发";
		for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
			fieldNode.setValue(s1 + i + s2);
			String fast = JSONObject.toJSONString(fieldNode).toLowerCase();
			String deep = DeepJson.toJsonString(fieldNode).toLowerCase();
			String gson = instance.toJson(fieldNode).toLowerCase();
			if (fast.equals(deep) && fast.equals(gson)) {
				continue;
			}
			System.out.println("value: " + fieldNode.getValue());
			System.out.println("fast: " + fast);
			System.out.println("deep: " + deep);
			System.out.println("gson: " + gson);
		}
	}
}