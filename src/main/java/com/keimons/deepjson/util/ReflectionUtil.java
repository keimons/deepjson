package com.keimons.deepjson.util;

import com.keimons.deepjson.internal.util.LookupUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

/**
 * 反射工具类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReflectionUtil {

	/**
	 * 创建实例
	 * <p>
	 * Java1.6-1.8中使用反射创建，Java9及以后使用MethodHandle。
	 *
	 * @param clazz 实例所属类
	 * @param <T>   实例类型
	 * @return 实例对象
	 * @throws Throwable 异常信息
	 */
	public static <T> T newInstance(Class<?> clazz) throws Throwable {
		if (PlatformUtil.javaVersion() < 9) {
			return newInstance0(clazz);
		} else {
			return newInstance1(clazz);
		}
	}

	/**
	 * 反射创建实例
	 *
	 * @param clazz 实例所属类
	 * @param <T>   实例类型
	 * @return 实例对象
	 * @throws Throwable 实例创建异常
	 */
	@SuppressWarnings("unchecked")
	private static <T> T newInstance0(Class<?> clazz) throws Throwable {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		return (T) constructor.newInstance();
	}

	/**
	 * {@code MethodHandle}创建实例
	 *
	 * @param clazz 实例所属类
	 * @param <T>   实例类型
	 * @return 实例对象
	 * @throws Throwable 实例创建异常
	 */
	@SuppressWarnings("unchecked")
	private static <T> T newInstance1(Class<?> clazz) throws Throwable {
		MethodHandle constructor = LookupUtils.lookup().findConstructor(clazz, MethodType.methodType(void.class));
		return (T) constructor.invoke();
	}
}