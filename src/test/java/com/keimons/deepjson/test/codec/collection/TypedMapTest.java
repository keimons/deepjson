package com.keimons.deepjson.test.codec.collection;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 已知类型字典编码器测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypedMapTest {

	@Test
	public void test() {
		Node node = new Node();
		node.value0.put("test0", 100);
		node.value0.put("test1", null);
		node.value0.put(null, 100);
		node.value1.put("test0", new Node());
		node.value1.put("test1", null);
		node.value1.put(null, new Node());
		node.value2.put("test0", TestEnum.TEST);
		node.value2.put("test1", null);
		node.value2.put("null", TestEnum.DEV);
		node.value3.put("test0", 100);
		node.value3.put("test1", 100);
		String json = DeepJson.toJsonString(node);
		System.out.println(json);
		String result = "{" +
				"\"value0\":{null:100,\"test0\":100,\"test1\":null}," +
				"\"value1\":{" +
				"null:{\"value0\":{},\"value1\":{},\"value2\":{},\"value3\":{}}," +
				"\"test0\":{\"value0\":{},\"value1\":{},\"value2\":{},\"value3\":{}}," +
				"\"test1\":null" +
				"}," +
				"\"value2\":{\"null\":\"DEV\",\"test0\":\"TEST\",\"test1\":null}," +
				"\"value3\":{\"test0\":100,\"test1\":100}" +
				"}";
		AssertUtils.assertEquals("已知类型字典编码器测试", result, json);
	}

	private static class Node {

		Map<String, Integer> value0 = new HashMap<String, Integer>();

		Map<String, Node> value1 = new HashMap<String, Node>();

		Map<String, TestEnum> value2 = new HashMap<String, TestEnum>();

		Map value3 = new HashMap<String, TestEnum>();
	}

	private enum TestEnum {
		TEST, DEV
	}
}