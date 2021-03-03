package com.keimons.deepjson.test.normal;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.INode;
import com.keimons.deepjson.util.UnsafeUtil;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.List;

// -Xlog:gc*=info -verbose:gc -XX:G1HeapRegionSize=32m -Xms2048m -Xmx2048m
public class NormalTest {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	long offset;

	{
		try {
			offset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	int times = 100_0000;

	INode node = new NormalNode();

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
	}

	@Test
	public void fastTest() {
		System.out.println(JSONObject.toJSONString(node));
		System.out.println(DeepJson.toJsonString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		for (int j = 0; j < 20; j++) {
			List<String> list = new ArrayList<>(times);
			long fastStart = System.nanoTime();
			for (int i = 0; i < times; i++) {
				list.add(JSONObject.toJSONString(node));
			}
			long fastTime = System.nanoTime() - fastStart;
			System.out.println("fast json using time: " + fastTime / 1000000f);
		}
	}

	@Test
	public void deepTest() {
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		for (int j = 0; j < 20; j++) {
			long deepStart = System.nanoTime();
			List<String> list = new ArrayList<>(times);
			for (int i = 0; i < times; i++) {
				list.add(DeepJson.toJsonString(node));
			}
			long deepTime = System.nanoTime() - deepStart;
			System.out.println("deep json using time: " + deepTime / 1000000f);
		}
	}

	public static void main(String[] args) {
		new NormalTest().deepTest();
	}
}