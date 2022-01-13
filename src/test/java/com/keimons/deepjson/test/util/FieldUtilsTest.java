package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.FieldUtils;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * 类中字段的工具类的测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FieldUtilsTest {

	@Test
	public void testFields() {
		Field[] fields = FieldUtils.getFields(Node0.class);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			AssertUtils.assertEquals("获取类中所有字段", "value" + i, field.getName());
		}
	}

	@Test
	public void testMultipleFields() {
		try {
			Field[] fields = FieldUtils.getFields(Node.class);
			System.err.println("测试失败");
		} catch (Exception e) {
			AssertUtils.assertEquals("获取类中所有字段", IllegalArgumentException.class, e.getClass());
		}
	}

	private static class Node0 {

		boolean value0;

		char value1;

		byte value2;

		short value3;

		int value4;

		long value5;

		float value6;

		double value7;
	}

	private static abstract class AbstractNode {

		int value;
	}

	private static class Node extends AbstractNode {

		int value;
	}
}