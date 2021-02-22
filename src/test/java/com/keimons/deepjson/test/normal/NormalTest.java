package com.keimons.deepjson.test.normal;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.serializer.INode;
import com.keimons.deepjson.serializer.NormalNode;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// -verbose:gc -Xms8192m -Xmx8192m
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

	int times = 1000_0000;

	INode node = new NormalNode();

	@Test
	public void test() {
		String json = JSONObject.toJSONString(node);
		System.out.println(json);
		byte[] object = (byte[]) unsafe.getObject(json, offset);
		System.out.println(object.length);
		System.out.println(Arrays.toString(object));
		System.out.println(DeepJson.toJsonString(node, SerializerOptions.IgnoreNonField));
	}

	@Test
	public void fastTest() {
		System.out.println(DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

//		for (int j = 0; j < 10; j++) {
			List<String> list = new ArrayList<>(times);
			long fastStart = System.nanoTime();
			for (int i = 0; i < times; i++) {
				list.add(JSONObject.toJSONString(node));
			}
			long fastTime = System.nanoTime() - fastStart;
			System.out.println("fast json using time: " + fastTime / 1000000f);
//		}
	}

	@Test
	public void deepTest() {
		System.out.println(DeepJson.toJsonString(node) == DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node) == JSONObject.toJSONString(node));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

//		for (int j = 0; j < 10; j++) {
			long deepStart = System.nanoTime();
			List<String> list = new ArrayList<>(times);
			for (int i = 0; i < times; i++) {
				list.add(DeepJson.toJsonString(node));
			}
			long deepTime = System.nanoTime() - deepStart;
			System.out.println("deep json using time: " + deepTime / 1000000f);
//		}
	}

	public static void main(String[] args) {
		new NormalTest().deepTest();
	}
}