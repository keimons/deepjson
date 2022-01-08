package com.keimons.deepjson.test.codec.collection;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 已知类型集合编码器测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypedCollectionTest {

	@Test
	public void test() {
		Node node = new Node();
		node.value0.addAll(Arrays.asList("test0", null, "test2"));
		node.value1.addAll(Arrays.asList(new Node(), null, new Node()));
		node.value2.addAll(Arrays.asList(TestEnum.TEST, null, TestEnum.DEV));
		String json = DeepJson.toJsonString(node);
		String result = "{\"value0\":[\"test0\",null,\"test2\"],\"value1\":[{\"value0\":[],\"value1\":[],\"value2\":[]},null,{\"value0\":[],\"value1\":[],\"value2\":[]}],\"value2\":[\"TEST\",null,\"DEV\"]}";
		AssertUtils.assertEquals("已知类型集合编码器测试", result, json);
	}

	private static class Node {

		List<String> value0 = new ArrayList<String>();

		List<Node> value1 = new ArrayList<Node>();

		List<TestEnum> value2 = new ArrayList<TestEnum>();
	}

	private enum TestEnum {
		TEST, DEV
	}
}