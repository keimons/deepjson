package com.keimons.deepjson.internal.util;

import com.keimons.deepjson.compiler.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类中字段工具类
 * <p>
 * 读取一个类或接口及其父类中的所有字段，包括公共、受保护、默认和私有字段。
 * 如果有同名字段，抛出{@link IllegalArgumentException}异常。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class FieldUtils {

	/**
	 * 获取一个类中的所有字段（包含父类、不包括静态字段）
	 * <p>
	 * 获取{@link Class}对象表示的类或接口及其父类声明的所有字段。包括公共、受保护、默认和私有字段。
	 * 返回数组中的元素没有排序，也没有任何特定的顺序。
	 *
	 * @param clazz 要获取所有字段的类
	 * @return 类中的所有字段
	 * @throws IllegalArgumentException 声明多个同名字段
	 * @see Class#getDeclaredFields() 该类的所有声明字段的Field对象数组
	 */
	public static Field[] getFields(Class<?> clazz) {
		Map<String, Field> result = new LinkedHashMap<String, Field>();
		Class<?> current = clazz;
		while (current != null && !current.equals(Object.class)) {
			Field[] fields = current.getDeclaredFields();
			for (Field field : fields) {
				// jump static field
				if (Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				String name = field.getName();
				if (result.containsKey(name)) {
					throw new IllegalArgumentException("declares multiple fields named [" + name + "] in " + clazz);
				}
				result.put(name, field);
			}
			current = current.getSuperclass();
		}
		return result.values().toArray(new Field[0]);
	}

	/**
	 * 获取类中所有属性
	 *
	 * @param clazz 获取属性的类
	 * @return 类中所有属性
	 * @throws IllegalArgumentException 声明多个同名字段
	 */
	public static List<Property> createProperties(Class<?> clazz) {
		Field[] fields = getFields(clazz);
		List<Property> properties = new ArrayList<Property>(fields.length);
		for (Field field : fields) {
			properties.add(new Property(field));
		}
		return properties;
	}
}