package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.Node;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 泛型数组测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class GenericArrayTest {

	AbstractGenericArrayNode<Node, Node, Node, HashMap<?, ?>> node = new AbstractGenericArrayNode<>();

	@Test
	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		node.value0 = new HashMap[]{new LinkedHashMap<>()};
		node.value1 = new HashMap[][]{new HashMap[]{new LinkedHashMap<>()}};

		node.value00 = new Node[]{Node.create()};
		node.value01 = new Node[][]{new Node[]{Node.create()}};

		node.value10 = new Node[]{Node.create()};
		node.value11 = new Node[][]{new Node[]{Node.create()}};

		node.value20 = new Node[]{Node.create()};
		node.value21 = new Node[][]{new Node[]{Node.create()}};

		node.value30 = new HashMap[]{new LinkedHashMap<>()};
		node.value31 = new HashMap[][]{new HashMap[]{new LinkedHashMap<>()}};

		String json = DeepJson.toJsonString(node);
		System.out.println(json);
		Type type = GenericArrayTest.class.getDeclaredField("node").getGenericType();
		AbstractGenericArrayNode result = DeepJson.parseObject(json, AbstractGenericArrayNode.class);
		System.out.println(result);
	}
}