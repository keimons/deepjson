package com.keimons.deepjson.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ArrayUtil {

	/**
	 * 创建一个数组
	 *
	 * @param clazz  数组中存放的元素
	 * @param length 数组长度
	 * @param <T>    数组中存放元素类型
	 * @return 新数组
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz, int length) {
		return (T) Array.newInstance(clazz, length);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> asList(T... a) {
		ArrayList<T> list = new ArrayList<T>(a.length);
		for (T item : a) {
			list.add(item);
		}
		return list;
	}

	/**
	 * 判断两个数组是否相等
	 *
	 * @param src    数组1
	 * @param dst    数组2
	 * @param length 长度
	 * @return 是否相等
	 */
	public static boolean isSame(char[] src, char[] dst, int length) {
		for (int i = 0; i < length; i++) {
			if (src[i] != dst[i]) {
				return false;
			}
		}
		return true;
	}

	public static int hashcode(char[] value, int startIndex, int endIndex) {
		if (value == null)
			return 0;
		int result = 1;
		for (int i = startIndex; i < endIndex; i++) {
			result = 31 * result + value[i];
		}
		return result;
	}
}