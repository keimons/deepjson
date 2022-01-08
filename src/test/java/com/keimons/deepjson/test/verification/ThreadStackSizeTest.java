package com.keimons.deepjson.test.verification;

import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

/**
 * 栈深验证
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.7
 **/
public class ThreadStackSizeTest {

	@Test
	public void test() {
		try {
			test();
		} catch (Error error) {
			AssertUtils.assertEquals("异常捕获失败", StackOverflowError.class, error.getClass());
			AssertUtils.assertTrue("栈深错误", error.getStackTrace().length == 1024);
		}
	}
}