package com.keimons.deepjson.util;

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
		Wrapper wrapper = Wrapper.findWrapper(className);
		if (wrapper == null || !wrapper.getPType().isPrimitive()) {
			return null;
		}
		return wrapper.getPType();
	}

	public static Class<?> findWrapperClass(Class<?> clazz) {
		if (clazz == int.class) {
			return Integer.class;
		}
		if (clazz == long.class) {
			return Long.class;
		}
		if (clazz == float.class) {
			return Float.class;
		}
		if (clazz == double.class) {
			return Double.class;
		}
		if (clazz == boolean.class) {
			return Boolean.class;
		}
		if (clazz == byte.class) {
			return Byte.class;
		}
		if (clazz == char.class) {
			return Character.class;
		}
		if (clazz == short.class) {
			return Short.class;
		}
		if (clazz == void.class) {
			return Void.class;
		}
		return clazz;
	}

	public static Object findDefaultValue(Class<?> clazz) {
		if (clazz == int.class) {
			return 0;
		}
		if (clazz == long.class) {
			return 0L;
		}
		if (clazz == float.class) {
			return 0F;
		}
		if (clazz == double.class) {
			return 0D;
		}
		if (clazz == boolean.class) {
			return false;
		}
		if (clazz == byte.class) {
			return (byte) 0;
		}
		if (clazz == char.class) {
			return '\u0000';
		}
		if (clazz == short.class) {
			return (short) 0;
		}
		return null;
	}

	/**
	 * 获取类型描述符
	 *
	 * @param clazz 类型
	 * @return 描述符
	 */
	public static char basicType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (void.class == clazz) {
				return 'V';
			} else if (int.class == clazz) {
				return 'I';
			} else if (boolean.class == clazz) {
				return 'Z';
			} else if (float.class == clazz) {
				return 'F';
			} else if (long.class == clazz) {
				return 'J';
			} else if (double.class == clazz) {
				return 'D';
			} else if (char.class == clazz) {
				return 'C';
			} else if (byte.class == clazz) {
				return 'B';
			} else if (short.class == clazz) {
				return 'S';
			}
			throw new IllegalStateException("unknown primitive type: " + clazz);
		}
		return 'L';
	}

	/**
	 * 根据类型描述符获取类型
	 * <ul>
	 *     <li>基础类型描述符返回基础类型</li>
	 *     <li>{@code 'L'}和{@code '['}返回空</li>
	 *     <li>其他抛出{@link IllegalStateException}异常</li>
	 * </ul>
	 *
	 * @param type 类型描述符
	 * @return 类型
	 */
	public static Class<?> basicType(char type) {
		switch (type) {
			case 'V':
				return void.class;
			case 'I':
				return int.class;
			case 'Z':
				return boolean.class;
			case 'F':
				return float.class;
			case 'J':
				return long.class;
			case 'D':
				return double.class;
			case 'C':
				return char.class;
			case 'B':
				return byte.class;
			case 'S':
				return short.class;
			case 'L':
			case '[':
				return null;
			default:
				throw new IllegalStateException("unknown primitive type: " + type);
		}
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

	/**
	 * 获取类文件的名称
	 * <p>
	 * 相对于包含的包：例如 "String.class"
	 *
	 * @param clazz 获取文件名的类
	 * @return 文件的文件名，以".class"结尾
	 */
	public static String getClassFileName(Class<?> clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(".");
		return className.substring(lastDotIndex + 1) + ".class";
	}
}