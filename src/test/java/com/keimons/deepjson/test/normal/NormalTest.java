package com.keimons.deepjson.test.normal;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.test.INode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

// -verbose:gc -Xms8192m -Xmx8192m
public class NormalTest {

	int times = 100_0000;

	INode node = new NormalNode();

	@Test
	public void test() {
		Integer[] integers = new Integer[10];
		integers[5] = 0;
		System.out.println(JSONObject.toJSONString(integers));
		System.out.println(DeepJson.toJsonString(integers));
		System.out.println(new Gson().toJson(integers));
		System.out.println(DeepJson.toJsonString(node, SerializerOptions.IgnoreNonField));
		System.out.println(JSONObject.toJSONString(node));
	}

	@Test
	public void fastTest() {
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);
		List<String> list = new ArrayList<>(times);
		long fastStart = System.nanoTime();
		for (int i = 0; i < times; i++) {
			list.add(JSONObject.toJSONString(node));
		}
		long fastTime = System.nanoTime() - fastStart;
		System.out.println("fast json using time: " + fastTime / 1000000f);
	}

	@Test
	public void deepTest() {
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		List<String> list = new ArrayList<>(times);
		long deepStart = System.nanoTime();
		for (int i = 0; i < times; i++) {
			list.add(DeepJson.toJsonString(node));
		}
		long deepTime = System.nanoTime() - deepStart;

		System.out.println("deep json using time: " + deepTime / 1000000f);
	}
}