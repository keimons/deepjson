package com.keimons.deepjson.test;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.INode;
import com.keimons.deepjson.NormalNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

// -verbose:gc -Xms8192m -Xmx8192m
public class DeepJsonTest {

	int times = 100_0000;

//	INode node = new IntNode();
//	INode node = new BoolNode();
	INode node = new NormalNode();

	@Test
	public void fastTest1() {
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
	public void deepTest2() {
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