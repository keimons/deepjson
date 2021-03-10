package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@link Object}测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ClassTest {

	@Test
	public void test() {
		System.out.println(JSONObject.toJSONString(ClassTest.class));
		System.out.println(DeepJson.toJsonString(ClassTest.class));
	}
}