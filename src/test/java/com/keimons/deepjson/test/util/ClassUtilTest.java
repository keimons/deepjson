package com.keimons.deepjson.test.util;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.internal.util.GenericUtil;
import com.keimons.deepjson.internal.util.Stack;
import com.keimons.deepjson.test.codec.collection.Node;
import com.keimons.deepjson.util.ClassUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ClassUtil}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassUtilTest {

	private HashMap<? super String, ? extends Number> value0;

	@Test
	public void test() throws Exception {
		CodecConfig.addWrite(TopNode.class);
		CodecConfig.addWrite(MidNode.class);
		CodecConfig.addWrite(BotNode.class);
		CodecConfig.addWrite(PT1Node.class);
		CodecConfig.addWrite(PT2Node.class);
		CodecConfig.addWrite(PT3Node.class);
		CodecConfig.addWrite(PT4Node.class);
		CodecConfig.addWrite(Node.class);
		CodecConfig.addWrite(InnerNode.class);
		Type type = ClassUtilTest.class.getDeclaredField("value0").getGenericType();
		Stack<Type> types = new Stack<Type>();
		types.push(ClassUtilTest.class.getDeclaredField("value0").getGenericType());
		Class<?> clazz = GenericUtil.findClass(types, type, null);
		Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
		System.out.println("map  clazz: " + clazz);
		System.out.println("map  key:   " + GenericUtil.findGenericType(types, Map.class, "K"));
		System.out.println("map  value: " + GenericUtil.findGenericType(types, Map.class, "V"));
		System.out.println("args key:   " + GenericUtil.findClass(types, arguments[0], null));
		System.out.println("args value: " + GenericUtil.findClass(types, arguments[1], null));

		types.poll();
		types.push(BotNode.class);
		System.out.println("node:  " + GenericUtil.findGenericType(types, TopNode.class, "T"));

		types.poll();
		types.push(MidNode.class);
		type = GenericUtil.findGenericType(types, TopNode.class, "T");
		if (type instanceof TypeVariable) {
			System.out.println("node:  " + Arrays.toString(((TypeVariable<?>) type).getBounds()));
		} else {
			System.out.println("node:  " + type);
		}

		BotNode node = new BotNode();
		node.value = new InnerNode<PT3Node>();
		node.value.innerValue0 = new PT3Node();
		node.value.innerValue1 = new PT3Node[4];
		node.value.innerValue2 = new PT3Node[1][];
		node.value.innerValue2[0] = new PT3Node[]{new PT3Node()};
		PT3Node pt3 = new PT3Node();
		node.topValue0 = pt3;
		node.topValue1 = new PT3Node[]{pt3, pt3};
		HashMap<PT4Node, PT2Node<Boolean>> tv2 = new HashMap<>();
		node.topValue2 = tv2;
		PT2Node<Boolean> pt2 = new PT2Node<Boolean>();
		PT4Node pt4 = new PT4Node();
		tv2.put(pt4, pt2);
		node.topValue10 = 123;
		node.topValue11 = new Integer[4];
		node.midValue0 = pt3;
		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		System.out.println(json);
		System.out.println(DeepJson.toJsonString(DeepJson.parseObject(json, BotNode.class), CodecOptions.WriteClassName));

		TopNode<PT3Node, Integer> topNode = new TopNode<PT3Node, Integer>();
		topNode.topValue2 = tv2;
		CodecConfig.clearWrite();
		json = DeepJson.toJsonString(topNode);
		System.out.println(json);
		TopNode tn = DeepJson.parseObject(json, TopNode.class);
		System.out.println(DeepJson.toJsonString(tn));

		System.out.println(tn.topValue0);
		System.out.println(tn.topValue1);
		System.out.println(tn.topValue2);
		System.out.println(tn.topValue10);
		System.out.println(tn.topValue11);
		System.out.println(tn.value);
	}
}