package com.keimons.deepjson.test.util;

import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.Config;
import com.keimons.deepjson.DeepJson;
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
		Config.addWrite(TopNode.class);
		Config.addWrite(MidNode.class);
		Config.addWrite(BotNode.class);
		Config.addWrite(PT1Node.class);
		Config.addWrite(PT2Node.class);
		Config.addWrite(PT3Node.class);
		Config.addWrite(PT4Node.class);
		Config.addWrite(Node.class);
		Config.addWrite(InnerNode.class);
		Type[] types = new Type[16];
		types[0] = ClassUtilTest.class.getDeclaredField("value0").getGenericType();
		Class<?> clazz = ClassUtil.findClass(types, 1, types[0], null);
		Type[] arguments = ((ParameterizedType) types[0]).getActualTypeArguments();
		System.out.println("map  clazz: " + clazz);
		System.out.println("map  key:   " + ClassUtil.findGenericType(types, 1, Map.class, "K"));
		System.out.println("map  value: " + ClassUtil.findGenericType(types, 1, Map.class, "V"));
		System.out.println("args key:   " + ClassUtil.findClass(types, 1, arguments[0], null));
		System.out.println("args value: " + ClassUtil.findClass(types, 1, arguments[1], null));

		types[0] = BotNode.class;
		System.out.println("node:  " + ClassUtil.findGenericType(types, 1, TopNode.class, "T"));

		types[0] = MidNode.class;
		Type type = ClassUtil.findGenericType(types, 1, TopNode.class, "T");
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
		Config.clearWrite();
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