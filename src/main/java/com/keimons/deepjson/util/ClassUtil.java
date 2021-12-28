package com.keimons.deepjson.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Class文件工具类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassUtil {

	/**
	 * 根据类型查找类型
	 * <p>
	 * TODO java 1.6 兼容性测试
	 *
	 * @param className 类名
	 * @return 类型
	 */
	public static Class<?> findClass(String className) {
		// 数组维数不超过255
		if (className.lastIndexOf("[") > 255) {
			throw new TypeNotFoundException("The number of dimensions of the new array must not exceed 255.");
		}
		// primitive type
		if (!className.contains(".")) {
			Class<?> clazz = findPrimitive(className);
			if (clazz != null) {
				return clazz;
			}
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			try {
				loader.loadClass(className);
			} catch (Exception e) {
				// ignore
			}
		}
		try {
			// 最后的查找方案，如果找不到，直接抛异常
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new TypeNotFoundException("class not found", e);
		}
	}

	/**
	 * 查找基础或者基础类型数组
	 *
	 * @param className 类型名
	 * @return 基础类型 或 基础类型数组
	 */
	private static Class<?> findPrimitive(String className) {
		if ("int".equals(className)) {
			return Integer.TYPE;
		}
		if ("long".equals(className)) {
			return Long.TYPE;
		}
		if ("float".equals(className)) {
			return Float.TYPE;
		}
		if ("double".equals(className)) {
			return Double.TYPE;
		}
		if ("boolean".equals(className)) {
			return Boolean.TYPE;
		}
		if ("byte".equals(className)) {
			return Byte.TYPE;
		}
		if ("char".equals(className)) {
			return Character.TYPE;
		}
		if ("short".equals(className)) {
			return Short.TYPE;
		}
		if ("void".equals(className)) {
			return Void.TYPE;
		}
		// 对于基础类型数组 java可以解析255维以内的数组，返回null，交由后续解析
		return null;
	}

	/**
	 * 获取一个类中的所有字段（包含父类）
	 *
	 * @param clazz 要获取所有字段的类
	 * @return 类中的所有字段
	 */
	public static List<Field> getFields(Class<?> clazz) {
		List<Field> result = new ArrayList<Field>();
		Class<?> current = clazz;
		while (current != null && !current.equals(Object.class)) {
			Field[] fields = current.getDeclaredFields();
			for (Field field : fields) {
				// jump static field
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				// 子类覆盖父类的字段
				result.add(field);
			}
			current = current.getSuperclass();
		}
		return result;
	}

	/**
	 * 判断一个类型是否{@code lambda}表达式
	 *
	 * @param clazz 类型
	 * @return 是否{@code lambda}表达式
	 */
	public static boolean isLambda(Class<?> clazz) {
		String name = clazz.getName();
		return name.contains("$$Lambda$") && name.contains("/");
	}
}