package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.LongAdder;

/**
 * 随机测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class AnyTest {

	@Test
	public void test() {
		LongAdder adder = new LongAdder();
		adder.increment();
		adder.increment();
		System.out.println(DeepJson.toJsonString(adder));
		System.out.println(JSONObject.toJSONString(adder));
		System.out.println(new Gson().toJson(adder));
	}
}