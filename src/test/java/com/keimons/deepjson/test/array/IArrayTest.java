package com.keimons.deepjson.test.array;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * int[] test
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class IArrayTest {

	private static final int[] array = new int[1000];

	private static final int testTimes = 10_0000;

	static {
		Random random = new Random();
		for (int i = 0; i < array.length; i++) {
			array[i] = random.nextInt();
		}
	}

	@Test
	public void test() {
		System.out.println("int[] 基础测试");
		System.out.println(DeepJson.toJsonString(array));
		System.out.println(JSONObject.toJSONString(array));
	}

	@Test
	public void deepTest() {
		long deepStart = System.nanoTime();
		for (int i = 0; i < testTimes; i++) {
			DeepJson.toJsonString(array);
		}
		long deepTime = System.nanoTime() - deepStart;
		System.out.println("int[] deep json using time: " + deepTime / 1000000f);
	}

	@Test
	public void fastTest() {
		long fastStart = System.nanoTime();
		for (int i = 0; i < testTimes; i++) {
			DeepJson.toJsonString(array);
		}
		long fastTime = System.nanoTime() - fastStart;
		System.out.println("int[] fast json using time: " + fastTime / 1000000f);
	}
}