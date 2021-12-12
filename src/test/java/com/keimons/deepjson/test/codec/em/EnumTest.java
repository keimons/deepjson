package com.keimons.deepjson.test.codec.em;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@link Enum}枚举测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EnumTest {

	@Test
	public void test() {
		Node node = new Node();
		node.value0 = TestEnum.TEST;
		node.value1 = TestEnum.TEST;
		node.value2 = TestEnum.DEV;
		node.value3 = TestEnum.DEV;
		String json = DeepJson.toJsonString(node);
		assert json.equals("{\"value0\":\"TEST\",\"value1\":\"TEST\",\"value2\":\"DEV\",\"value3\":\"DEV\"}");
	}

	private static class Node {

		TestEnum value0;

		TestEnum value1;

		TestEnum value2;

		TestEnum value3;
	}
}