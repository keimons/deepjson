package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.Node;
import org.junit.jupiter.api.Test;

/**
 * 对象数组类型编解码测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ArrayTest {

	@Test
	public void test() throws NoSuchFieldException {
		System.out.println("----------------> Array Test Encode <----------------");
		ArrayNode node = new ArrayNode();
		node.value0 = Node.array1();
		node.value1 = Node.array2();
		String json1 = DeepJson.toJsonString(node);
		System.out.println("encode: " + json1);
		String json2 = DeepJson.toJsonString(node);
		System.out.println("encode: " + json2);
		System.out.println("----------------> Array Test Decode <----------------");
		ArrayNode result1 = DeepJson.parseObject(json1, ArrayNode.class);
		System.out.println("decode: " + DeepJson.toJsonString(result1));
		System.out.println("decode: " + result1.value0.getClass());
		System.out.println("decode: " + result1.value1.getClass());
		ArrayNode result2 = DeepJson.parseObject(json2, ArrayNode.class);
		System.out.println("decode: " + DeepJson.toJsonString(result2));
		System.out.println("decode: " + result2.value0.getClass());
		System.out.println("decode: " + result2.value1.getClass());
	}
}