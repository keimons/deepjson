package com.keimons.deepjson.test.codec.ref;

import com.keimons.deepjson.Config;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.Node;
import com.keimons.deepjson.util.ReflectUtil;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link TypeVariable}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypeVariableTest {

	HashMap<String, Node> map;

	@Test
	public void test() throws Exception {
		ParameterizedType pt = ReflectUtil.makeParameterizedType(null, Map.class, new Type[]{String.class, Node.class});
		Field field = TypeVariableTest.class.getDeclaredField("map");
		Config.registerMapper(new Type[]{pt, Serializable.class}, field.getGenericType()); // same as HashMap<String, Node>

//		Config.registerMapper(new Type[]{Node.class, Serializable.class}, Node.class); // same as HashMap<String, Node>

		TVNode node = new TVNode();
		node.value0 = new Node();
		node.value1 = new Boolean[]{true, false};
		node.value2 = "100";
		node.value3 = 20;
		node.value4 = new HashMap<String, Node>();
		node.value4.put("test1", Node.create());
		node.value4.put("test2", Node.create());
		node.value4.put("test3", Node.create());
		node.value5 = new Node();
		System.out.println("----------------> TypeVariable Test Encode <----------------");
		String json = DeepJson.toJsonString(node);
		System.out.println("encode: " + json);
		System.out.println("----------------> TypeVariable Test Decode <----------------");
		json = "{\"value0\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444},\"value1\":[true,false],\"value2\":\"100\",\"value3\":20,\"value4\":{\"$type\":\"java.util.LinkedHashMap\",\"test2\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444},\"test3\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444},\"test1\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444}},\"value5\":{\"value0\":true,\"value1\":111,\"value2\":222,\"value3\":333,\"value4\":444}}";
		TVNode result = DeepJson.parseObject(json, TVNode.class);
		String js = DeepJson.toJsonString(result);
		System.out.println("decode: " + js);
	}
}