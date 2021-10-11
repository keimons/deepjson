package com.keimons.deepjson.test.codec.parameterized;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.util.ReflectUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * {@link ParameterizedType}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ParameterizedTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test() {
		Node<String, Integer> node = new Node<String, Integer>();
		node.value0 = new HashMap<String, Integer>();
		node.value1 = 1024;
		node.value2 = "test";
		node.value3 = 256;
		String json = DeepJson.toJsonString(node);
		System.out.println("----------------> ParameterizedType Test <----------------");
		System.out.println("encode: " + json);
		Type t1 = ReflectUtil.makeTypeVariable(Node.class, "T1", new Type[]{String.class});
		Type t2 = ReflectUtil.makeTypeVariable(Node.class, "T2", new Type[]{Integer.class});
		ParameterizedType pt = ReflectUtil.makeParameterizedType(null, Node.class, t1, t2);
		Node<String, Integer> result1 = DeepJson.parseObject(json, Node.class);
		Node<String, Integer> result2 = DeepJson.parseObject(json, pt);
		System.out.println("decode: " + result1);
		System.out.println("decode: " + result2);
	}
}