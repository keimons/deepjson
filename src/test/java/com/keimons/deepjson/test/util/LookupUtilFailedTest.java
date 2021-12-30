package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.LookupUtil;
import com.keimons.deepjson.test.AssertUtil;
import com.keimons.deepjson.util.IllegalCallerException;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

/**
 * {@link LookupUtil}失败测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class LookupUtilFailedTest {

	/**
	 * 调用测试
	 *
	 * @since 1.6
	 */
	@Test
	public void testVersion() {
		// version less 9
		try {
			MethodHandles.Lookup lookup = LookupUtil.lookup();
		} catch (Exception e) {
			AssertUtil.assertEquals("查找类不可用失败测试", IllegalCallerException.class, e.getClass());
		}
	}

	/**
	 * 调用测试
	 *
	 * @since 1.6
	 */
	@Test
	public void testCaller() {
		// version less 9
		try {
			LookupUtil.lookup();
		} catch (Exception e) {
			AssertUtil.assertEquals("查找类不可用失败测试", IllegalCallerException.class, e.getClass());
		}
	}
}