package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.Stack;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

/**
 * {@link Stack}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StackTest {

	@Test
	public void test() {
		Stack<Integer> stack = new Stack<Integer>();
		for (int i = 0; i < 64; i++) {
			stack.push(i);
		}
		int number = 63;
		for (Integer value : stack) {
			AssertUtils.assertTrue("Stack工具类测试", value == number--);
		}
		AssertUtils.assertTrue("Stack工具类测试", stack.poll() == 63);
		AssertUtils.assertTrue("Stack工具类测试", stack.size() == 63);
		AssertUtils.assertTrue("Stack工具类测试", stack.poll() == 62);
		AssertUtils.assertTrue("Stack工具类测试", stack.size() == 62);
	}
}