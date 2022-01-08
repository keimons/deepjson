package com.keimons.deepjson.test.codec.generic;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtils;
import com.keimons.deepjson.util.TypeUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ParameterizedType}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ParameterizedTypeTest {

	@Test
	public void test() throws NoSuchFieldException {
		Node<Integer, String> node = new Node<Integer, String>();
		node.value.put("1024", 1024);
		node.value0 = "1024";
		node.value1 = 1024;

		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("泛型参数测试", "{\"value\":{\"1024\":1024},\"value0\":\"1024\",\"value1\":1024}", json);
		@SuppressWarnings("rawtypes")
		Node result0 = DeepJson.parseObject("{\"value\":{\"1024\":1024},\"value0\":\"1024\",\"value1\":1024}", Node.class);
		AssertUtils.assertEquals("泛型参数测试", "{\"value\":{\"1024\":1024},\"value0\":\"1024\",\"value1\":1024}", DeepJson.toJsonString(result0));

		ParameterizedType pt = TypeUtil.makeType(Node.class, Integer.class, Integer.class);
		Node<Integer, Integer> result1 = DeepJson.parseObject(json, pt);
		for (Map.Entry<Integer, Integer> entry : result1.value.entrySet()) {
			AssertUtils.assertTrue("泛型参数测试", entry.getKey() == 1024);
			AssertUtils.assertTrue("泛型参数测试", entry.getValue() == 1024);
		}
		AssertUtils.assertTrue("泛型参数测试", result1.value0 == 1024);
		AssertUtils.assertTrue("泛型参数测试", result1.value1 == 1024);
	}

	/**
	 * 测试节点
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 **/
	public static class AbstractNode<T1, T2> {

		public Map<T1, T2> value = new HashMap<T1, T2>();

		public T1 value0;

		public T2 value1;
	}

	/**
	 * 测试节点
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 **/
	public static class Node<T1, T2> extends AbstractNode<T2, T1> {

	}
}