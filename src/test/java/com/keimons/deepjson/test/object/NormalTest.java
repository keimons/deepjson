package com.keimons.deepjson.test.object;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

/**
 * 普通对象测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NormalTest {

	@Test
	public void test() {
		String expected = "{\"value0\":false,\"value1\":\"\\u0000\",\"value2\":0,\"value3\":0,\"value4\":0,\"value5\":0,\"value6\":0.0,\"value7\":0.0}";
		String json = DeepJson.toJsonString(new Node());
		AssertUtils.assertEquals("基础类型对象测试", expected, json);
	}

	public static class Node {

		private boolean value0;

		private char value1;

		private byte value2;

		private short value3;

		private int value4;

		private long value5;

		private float value6;

		private double value7;
	}
}