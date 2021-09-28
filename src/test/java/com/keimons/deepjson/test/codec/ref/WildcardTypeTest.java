package com.keimons.deepjson.test.codec.ref;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * {@link WildcardType}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class WildcardTypeTest {

	@Test
	public void test() {
		WTNode node = new WTNode();
		node.value0 = Arrays.asList(1, 2, 3, 4);
		node.value1.add(100);
		node.value2 = Arrays.asList("test0", "test1");
		node.value3.add("100");
		System.out.println("----------------> Wildcard Test Encode <----------------");
		String json = DeepJson.toJsonString(node);
		System.out.println("encode: " + json);
		System.out.println("----------------> Wildcard Test Decode <----------------");
		WTNode result = DeepJson.parseObject(json, WTNode.class);
		String js = DeepJson.toJsonString(result);
		System.out.println("decode: " + js);
	}
}