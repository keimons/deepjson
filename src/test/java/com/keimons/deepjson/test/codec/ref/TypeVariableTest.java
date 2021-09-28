package com.keimons.deepjson.test.codec.ref;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.Node;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;

/**
 * {@link TypeVariable}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypeVariableTest {

	@Test
	public void test() {
		TVNode<Node> node = new TVNode<Node>();
		node.value0 = new Node();
		node.value1 = "100";
		node.value2 = 20;
		System.out.println("----------------> TypeVariable Test Encode <----------------");
		String json = DeepJson.toJsonString(node);
		System.out.println("encode: " + json);
		System.out.println("----------------> TypeVariable Test Decode <----------------");
		TVNode result = DeepJson.parseObject(json, TVNode.class);
		String js = DeepJson.toJsonString(result);
		System.out.println("decode: " + js);
	}
}