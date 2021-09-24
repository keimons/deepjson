package com.keimons.deepjson.test.codec.collection;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link Set}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SetTest {

	LinkedHashSet<? super Node> list = new LinkedHashSet<Node>();

	@Test
	public void test() throws NoSuchFieldException {
		Field field = SetTest.class.getDeclaredField("list");
		System.out.println("----------------> Set Test <----------------");
		Node node = new Node(2);
		node.value1 = new Node(0);
		list.add(node);
		list.add(new Node(3));
		list.add(node.value1);
		list.add(new Node(1));
		// 2301
		String json = DeepJson.toJsonString(list);
		System.out.println("encode: " + json);
		Set<Node> result = DeepJson.parseObject(json, field.getGenericType());
		System.out.println("decode: " + DeepJson.toJsonString(result));
	}
}