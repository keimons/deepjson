package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.LookupUtils;
import com.keimons.deepjson.test.AssertUtils;
import com.keimons.deepjson.util.IllegalCallerException;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

/**
 * {@link LookupUtils}失败测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class LookupUtilsFailedTest {

	/**
	 * 调用测试
	 *
	 * @since 1.6
	 */
	@Test
	public void testVersion() {
		// version less 9
		try {
			MethodHandles.Lookup lookup = LookupUtils.lookup();
		} catch (Exception e) {
			AssertUtils.assertEquals("查找类不可用失败测试", IllegalCallerException.class, e.getClass());
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
			LookupUtils.lookup();
		} catch (Exception e) {
			AssertUtils.assertEquals("查找类不可用失败测试", IllegalCallerException.class, e.getClass());
		}
	}
}