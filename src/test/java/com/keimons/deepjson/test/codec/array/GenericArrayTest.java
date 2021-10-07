package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.CodecOptions;
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

	AbstractGenericArrayNode<Node, Node, Node, HashMap<String, Integer>> node = new AbstractGenericArrayNode<>();

	@Test
	public void test() throws Exception {
		node.value0 = new HashMap[1];
		node.value0[0] = new LinkedHashMap<>();
		node.value0[0].put("test", Node.create());
		node.value1 = new HashMap[1][];
		node.value1[0] = node.value0;
		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		System.out.println(json);
		json = "{\"value0\":{\"$type\":\"[Ljava.util.HashMap;\",\"@id\":1,\"$value\":[{\"test\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444}}]},\"value1\":[\"$id:1\"],\"value00\":null,\"value01\":null,\"value10\":null,\"value11\":null,\"value20\":null,\"value21\":null,\"value30\":null,\"value31\":null}";
		Type type = GenericArrayTest.class.getDeclaredField("node").getGenericType();
		AbstractGenericArrayNode result = DeepJson.parseObject(json, type);
		System.out.println(result);
	}
}