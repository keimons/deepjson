package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

public class ArrayTest {

	@Test
	public void test() throws NoSuchFieldException {
		System.out.println(Object[].class.isAssignableFrom(boolean[].class));
		ArrayNode node = new ArrayNode();
		node.value0 = new Boolean[1];
		node.value1 = new Boolean[1][];
		node.value1[0] = new Boolean[1];
		String json = DeepJson.toJsonString(node);
		System.out.println(json);
		ArrayNode result = DeepJson.parseObject(json, ArrayNode.class);
		System.out.println(DeepJson.toJsonString(result));
	}
}
