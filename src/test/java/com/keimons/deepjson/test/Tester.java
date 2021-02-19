package com.keimons.deepjson.test;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tester {

	static int times = 100_0000;

	//	INode node = new IntNode();
//	INode node = new BoolNode();
	static INode node = new NormalNode();

	public static void main(String[] args) throws NoSuchFieldException, ClassNotFoundException {
		Unsafe unsafe = UnsafeUtil.getUnsafe();
		Field value = String.class.getDeclaredField("value");
		long offset = unsafe.objectFieldOffset(value);
		System.out.println(Arrays.toString((byte[]) unsafe.getObject("我们a的", offset)));

		Class<?> clazz = Class.forName("java.lang.StringUTF16");
		System.out.println(unsafe.getInt(clazz, unsafe.staticFieldOffset(clazz.getDeclaredField("LO_BYTE_SHIFT"))));
		System.out.println(FillerHelper.LO_BYTE_SHIFT);

		System.out.println('a' & 0xFFFF);

		System.out.println(DeepJson.toJsonString(node));
		System.out.println(JSONObject.toJSONString(node));
		System.out.println(Arrays.toString((byte[]) unsafe.getObject(DeepJson.toJsonString(node), offset)));
		System.out.println(Arrays.toString((byte[]) unsafe.getObject(JSONObject.toJSONString(node), offset)));
		DeepJson.toJsonString(node);
		JSONObject.toJSONString(node);

		List<String> list = new ArrayList<>(times);
		long deepStart = System.nanoTime();
		for (int i = 0; i < times; i++) {
			list.add(JSONObject.toJSONString(node));
//			list.add(DeepJson.toJsonString(node));
		}
		long deepTime = System.nanoTime() - deepStart;

		System.out.println("deep json using time: " + deepTime / 1000000f);
	}
}