package com.keimons.deepjson.util;

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
	 * 反射工具
	 * <p>
	 * Java1.6-1.8中使用{@link ReflectionUtil}，java9及以后使用{@link MethodHandleUtil}。
	 */
	public static final ReflectionUtil instance;

	static {
		if (PlatformUtil.javaVersion() >= 9) {
			Class<?> clazz = null;
			try {
				clazz = Class.forName("com.keimons.deepjson.util.MethodHandleUtil");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			assert clazz != null;
			ReflectionUtil mh = null;
			try {
				mh = (ReflectionUtil) clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			instance = mh;
		} else {
			instance = new ReflectionUtil();
		}
	}

	/**
	 * 创建实例
	 * <p>
	 * Java1.6-1.8中使用反射创建，java9及以后使用MethodHandle。
	 *
	 * @param clazz 实例所属类
	 * @param <T>   实例类型
	 * @return 实例对象
	 * @throws Throwable 异常信息
	 */
	public static <T> T newInstance(Class<?> clazz) throws Throwable {
		return instance.newInstance0(clazz);
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
	public <T> T newInstance0(Class<?> clazz) throws Throwable {
		Constructor<?> constructor = clazz.getDeclaredConstructor();
		return (T) constructor.newInstance();
	}
}