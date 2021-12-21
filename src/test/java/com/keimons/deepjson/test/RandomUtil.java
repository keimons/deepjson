package com.keimons.deepjson.test;

import java.util.Random;

/**
 * 随机数
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see AssertionError 断言错误
 * @since 1.6
 */
public class RandomUtil {

	private static final Random RANDOM = new Random();

	public static int nextInt() {
		return RANDOM.nextInt();
	}
}