package com.keimons.deepjson.test.string;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * String类型测试
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class StringTest {

	@Test
	public void test() {
		String value = "deep ";

		for (int i = 0; i < 128; i++) {
			value += (char) i;
		}
		value += " json";

		System.out.println(DeepJson.toJsonString(value));
		System.out.println(JSONObject.toJSONString(value));
		System.out.println(new Gson().toJson(value));
	}
}