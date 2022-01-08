package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.LookupUtil;
import com.keimons.deepjson.test.AssertUtils;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

/**
 * {@link LookupUtil}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class LookupUtilTest {

	@Test
	public void testPackagePrivateClass() throws ClassNotFoundException {
		Class<?> clazz = Class.forName("java.lang.StringUTF16");

		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle handle = lookup.findStatic(clazz, "toBytes", MethodType.methodType(byte[].class, char.class));
			byte[] bytes = (byte[]) handle.invoke('a');
			System.out.println("测试通过：" + Arrays.toString(bytes));
		} catch (Throwable e) {
			AssertUtils.assertEquals("包私有类权限测试", IllegalAccessException.class, e.getClass());
		}

		try {
			// 发生警告
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
			MethodHandle handle = lookup.findStatic(clazz, "toBytes", MethodType.methodType(byte[].class, char.class));
			byte[] bytes = (byte[]) handle.invoke('a');
			AssertUtils.assertTrue("包私有类权限测试", bytes[0] == 'a');
			AssertUtils.assertTrue("包私有类权限测试", bytes[1] == 0);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			MethodHandles.Lookup lookup = LookupUtil.lookup();
			MethodHandle handle = lookup.findStatic(clazz, "toBytes", MethodType.methodType(byte[].class, char.class));
			byte[] bytes = (byte[]) handle.invoke('a');
			AssertUtils.assertTrue("包私有类权限测试", bytes[0] == 'a');
			AssertUtils.assertTrue("包私有类权限测试", bytes[1] == 0);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testPackagePrivateMethod() {
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle handle = lookup.findConstructor(String.class, MethodType.methodType(void.class, byte[].class, byte.class));
			String result = (String) handle.invoke(new byte[]{'k', 'e', 'i', 'm', 'o', 'n', 's'}, (byte) 0);
			System.out.println("测试通过：" + result);
		} catch (Throwable e) {
			AssertUtils.assertEquals("包私有方法权限测试", IllegalAccessException.class, e.getClass());
		}

		try {
			// 发生警告
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(String.class, MethodHandles.lookup());
			MethodHandle handle = lookup.findConstructor(String.class, MethodType.methodType(void.class, byte[].class, byte.class));
			String result = (String) handle.invoke(new byte[]{'k', 'e', 'i', 'm', 'o', 'n', 's'}, (byte) 0);
			AssertUtils.assertEquals("包私有方法权限测试", "keimons", result);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			MethodHandles.Lookup lookup = LookupUtil.lookup();
			MethodHandle handle = lookup.findConstructor(String.class, MethodType.methodType(void.class, byte[].class, byte.class));
			String result = (String) handle.invoke(new byte[]{'k', 'e', 'i', 'm', 'o', 'n', 's'}, (byte) 0);
			AssertUtils.assertEquals("包私有方法权限测试", "keimons", result);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNotOpenModule() throws ClassNotFoundException {
		Class<?> clazz = Class.forName("jdk.internal.misc.VM");
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle handle = lookup.findStaticGetter(clazz, "lock", Object.class);
			Object result = handle.invoke();
			System.out.println("测试通过：" + result);
		} catch (Throwable e) {
			AssertUtils.assertEquals("未开放模块测试", IllegalAccessException.class, e.getClass());
		}

		try {
			// 发生警告
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
			MethodHandle handle = lookup.findStaticGetter(clazz, "lock", Object.class);
			Object result = handle.invoke();
			System.out.println("测试通过：" + result);
		} catch (Throwable e) {
			AssertUtils.assertEquals("未开放模块测试", IllegalAccessException.class, e.getClass());
		}

		try {
			MethodHandles.Lookup lookup = LookupUtil.lookup();
			MethodHandle handle = lookup.findStaticGetter(clazz, "lock", Object.class);
			Object result = handle.invoke();
			AssertUtils.assertEquals("未开放模块测试", Object.class, result.getClass());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}