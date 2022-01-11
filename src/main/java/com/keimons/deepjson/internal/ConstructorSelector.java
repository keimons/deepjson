package com.keimons.deepjson.internal;

import java.lang.reflect.Constructor;

/**
 * 构造器选择
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface ConstructorSelector {

	/**
	 * 构造器中选择一个构造器
	 * <p>
	 * 注意：{@link Class#getDeclaredConstructors()}返回的构造方法是无序的。
	 * 采用读取字节码文件的方式获取所有构造方法，以保证顺序。
	 *
	 * @param constructors 所有构造器
	 * @return 使用的构造器
	 */
	Constructor<?> select(Constructor<?>[] constructors);
}