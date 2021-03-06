package com.keimons.deepjson.test.normal;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.INode;
import com.keimons.deepjson.test.NormalNode;
import com.keimons.deepjson.util.UnsafeUtil;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

// -Xlog:gc*=info -verbose:gc -XX:G1HeapRegionSize=32m -Xms2048m -Xmx2048m
public class NormalTest {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	long offset;

	int times = 100_0000;

	INode node = new NormalNode();

	{
		try {
			offset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		NormalNode cache = (NormalNode) node;
	}

	@Test
	public void test() {
		long nanoTime = System.nanoTime();
		System.out.println(JSONObject.toJSONString(node));
		float usingTime = (System.nanoTime() - nanoTime) / 1000000f;
		System.out.println("fast json first serializer using time: " + usingTime);

		nanoTime = System.nanoTime();
		System.out.println(DeepJson.toJsonString(node));
		usingTime = (System.nanoTime() - nanoTime) / 1000000f;
		System.out.println("deep json first serializer using time: " + usingTime);

		nanoTime = System.nanoTime();
		Gson gson = new Gson();
		System.out.println(gson.toJson(node));
		usingTime = (System.nanoTime() - nanoTime) / 1000000f;
		System.out.println("gson json first serializer using time: " + usingTime);
	}

	@Test
	public void fastTest() {
		System.out.println(JSONObject.toJSONString(node));
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(new Gson().toJson(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		for (int j = 0; j < 20; j++) {
			long fastStart = System.nanoTime();
			for (int i = 0; i < times; i++) {
				JSONObject.toJSONString(node);
			}
			long fastTime = System.nanoTime() - fastStart;
			System.out.println("fast json using time: " + fastTime / 1000000f);
		}
	}

	@Test
	public void deepTest() {
		System.out.println(JSONObject.toJSONString(node));
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(new Gson().toJson(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		for (int j = 0; j < 20; j++) {
			long deepStart = System.nanoTime();
			for (int i = 0; i < times; i++) {
				DeepJson.toJsonString(node);
			}
			long deepTime = System.nanoTime() - deepStart;
			System.out.println("deep json using time: " + deepTime / 1000000f);
		}
	}

	public static void main(String[] args) {
		new NormalTest().deepTest();
	}
}