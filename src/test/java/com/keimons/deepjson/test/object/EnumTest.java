package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@link Enum}序列化测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class EnumTest {

	@Test
	public void test() {
		System.out.println(JSONObject.toJSONString(EnumNode.DEBUG));
		System.out.println(DeepJson.toJsonString(EnumNode.DEBUG));
		System.out.println(new Gson().toJson(EnumNode.DEBUG));
	}
}