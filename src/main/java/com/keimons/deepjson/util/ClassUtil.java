package com.keimons.deepjson.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public static Type findGenericType(Type[] types, int length, Field field) {
		Type ft = field.getGenericType();
		if (ft instanceof GenericArrayType) {
			Type ct = ((GenericArrayType) ft).getGenericComponentType();
			if (ct instanceof TypeVariable) {
				TypeVariable<?> variable = (TypeVariable<?>) ct;
				Class<?> target = (Class<?>) variable.getGenericDeclaration();
				String name = variable.getName();
				Class<?> clazz = (Class<?>) findGenericType(types, length, target, name);
				return Array.newInstance(clazz, 0).getClass();
			}
			return ft;
		}
		if (ft instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) ft;
			Class<?> target = (Class<?>) variable.getGenericDeclaration();
			String name = variable.getName();
			return findGenericType(types, length, target, name);
		}
		return ft;
	}

	/**
	 * 在类中查找泛型类型
	 *
	 * @param types  查找起始位置{@link Class}或{@link ParameterizedType}。
	 * @param target 查找目标
	 * @param name   查找名称
	 * @return {@link Type}泛型类型。
	 */
	public static Type findGenericType(Type[] types, int length, Class<?> target, String name) {
		for (int i = length - 1; i >= 0; i--) {
			Type type = types[i];
			// 跳过泛型数组，只需要泛型数组的组件类型
			if (type instanceof GenericArrayType) {
				continue;
			}
			// 跳过对象数组，只需要对象数组的组件类型
			if (type instanceof Class && ((Class<?>) type).isArray()) {
				continue;
			}
			Type result = findGenericType(type, target, name);
			// 依然是泛型 继续向上查找
			if (result instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) result;
				name = tv.getName();
				target = (Class<?>) tv.getGenericDeclaration();
				continue;
			}
			if (result == null) {
				return Object.class; // 不存在的泛型类型
			}
			if (result instanceof WildcardType) {
				WildcardType wildcardType = (WildcardType) result;
				// 上界通配符
				Type[] upperBounds = wildcardType.getUpperBounds();
				if (upperBounds.length == 1 && upperBounds[0] != Object.class) {
					return upperBounds[0];
				}
				// 下界通配符
				Type[] lowerBounds = wildcardType.getLowerBounds();
				if (lowerBounds.length == 1 && lowerBounds[0] != Object.class) {
					return lowerBounds[0];
				}
				return Object.class;
			}
			return result;
		}
		return Object.class;
	}

	/**
	 * 在类中查找泛型类型
	 * <p>
	 * 注意：该方法仅仅适用于类继承和类字段，因为类型擦除的缘故，无法在方法中的变量中查找到泛型类型。例如：
	 * <pre>
	 *     public void test() {
	 *         Map&lt;String, Integer&gt; map = ...
	 *         Type type = findGenericType(map.getClass(), Map.class, "K");
	 *         assert type == null;
	 *     }
	 * </pre>
	 * <p>
	 * 在泛型传递过程中，最坑的是有可能泛型名被改了，所以，我们需要递归的查找泛型的实际类型。
	 * <pre>
	 *     class Node1&lt;S&gt; extends HashMap&lt;S, Integer&gt; {}
	 *
	 *     class Node2 extends Node1&lt;String&gt; {}
	 *
	 *     class Node3 extends Node2 {}
	 * </pre>
	 * 如果要在{@code Node3}中查找{@code K}的实际类型，需要采用递归的方法，首先找到{@code HashMap}
	 * 中的泛型{@code S}，然后，在{@code Node2}中查找{@code S}对应的实际类型{@code String}并返回。
	 *
	 * @param type   查找起始位置{@link Class}或{@link ParameterizedType}。
	 * @param target 查找目标，例如{@link Map}。
	 * @param name   查找名称，例如{@link Map}中的{@code K}。
	 * @return 泛型类型
	 * <ul>
	 *     <li>{@link ParameterizedType}依然是带有泛型的子类型，例如：{@code HashMap<String, Integer>}。</li>
	 *     <li>{@link TypeVariable}依然是泛型，例如：{@link Map}中的{@code K}，对应{@link Object}类型。</li>
	 *     <li>{@link Class} 内部对象类型</li>
	 *     <li>{@code null} 查找失败。</li>
	 * </ul>
	 */
	public static @Nullable Type findGenericType(Type type, Class<?> target, String name) {
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Class<?> clazz = (Class<?>) pt.getRawType();
			if (clazz != target) {
				// 开始递归调用
				Type result = findGenericType(clazz, target, name);
				if (result == null) {
					return null; // 查找失败
				} else {
					if (result instanceof Class) {
						// 已经成功找到
						return result;
					} else {
						// 在父类中找到了，但是依然是泛型，需要替换成原始类型
						name = result.getTypeName();
					}
				}
			}
			Type[] arguments = pt.getActualTypeArguments();
			TypeVariable<? extends Class<?>>[] variables = clazz.getTypeParameters();
			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getTypeName().equals(name)) {
					return arguments[i];
				}
			}
		}
		if (type instanceof Class) {
			Class<?> clazz = (Class<?>) type;
			Type superclass = clazz.getGenericSuperclass();
			if (superclass != null && !Object.class.equals(superclass)) {
				// 递归调用
				Type inner = findGenericType(superclass, target, name);
				if (inner != null) {
					return inner;
				}
			}

			Type[] interfaces = clazz.getGenericInterfaces();
			for (Type itf : interfaces) {
				Type inner = findGenericType(itf, target, name);
				if (inner != null) {
					return inner;
				}
			}
		}
		return null;
	}

	/**
	 * 查找一个对象的泛型类型
	 *
	 * @param target   对象
	 * @param clazz    类型
	 * @param typeName 泛型参数名
	 * @return 类型
	 */
	public static <T> Class<T> findGenericTypeOld(final Class<?> target, Class<?> clazz, String typeName) {
		if (clazz.isInterface()) {
			return findGenericTypeInInterface0(target, clazz, typeName);
		} else {
			return findGenericTypeInClass0(target, clazz, typeName);
		}
	}

	/**
	 * 在类中查找泛型类型
	 *
	 * @param clazz    要查找泛型的类
	 * @param typeName 查找的泛型字段名
	 * @param <T>      类类型
	 * @return 类
	 */
	@SuppressWarnings("unchecked")
	private static <T> Class<T> findGenericTypeInClass0(Class<?> target, Class<?> clazz, String typeName) {
		Class<?> currentClass = target;
		for (; ; ) {
			if (currentClass.getSuperclass() == clazz) {
				int typeParamIndex = -1;
				TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
				for (int i = 0; i < typeParams.length; i++) {
					if (typeName.equals(typeParams[i].getName())) {
						typeParamIndex = i;
						break;
					}
				}

				if (typeParamIndex < 0) {
					throw new IllegalStateException(
							"unknown type parameter '" + typeName + "': " + clazz);
				}

				Type genericSuperType = currentClass.getGenericSuperclass();
				if (!(genericSuperType instanceof ParameterizedType)) {
					return (Class<T>) Object.class;
				}

				Type[] actualTypeParams = ((ParameterizedType) genericSuperType).getActualTypeArguments();

				Type actualTypeParam = actualTypeParams[typeParamIndex];
				if (actualTypeParam instanceof ParameterizedType) {
					actualTypeParam = ((ParameterizedType) actualTypeParam).getRawType();
				}
				if (actualTypeParam instanceof Class) {
					return (Class<T>) actualTypeParam;
				}
				if (actualTypeParam instanceof GenericArrayType) {
					Type componentType = ((GenericArrayType) actualTypeParam).getGenericComponentType();
					if (componentType instanceof ParameterizedType) {
						componentType = ((ParameterizedType) componentType).getRawType();
					}
					if (componentType instanceof Class) {
						return (Class<T>) Array.newInstance((Class<?>) componentType, 0).getClass();
					}
				}
				if (actualTypeParam instanceof TypeVariable) {
					// Resolved type parameter points to another type parameter.
					TypeVariable<?> v = (TypeVariable<?>) actualTypeParam;
					currentClass = target;
					if (!(v.getGenericDeclaration() instanceof Class)) {
						return (Class<T>) Object.class;
					}

					clazz = (Class<?>) v.getGenericDeclaration();
					typeName = v.getName();
					if (clazz.isAssignableFrom(target)) {
						continue;
					} else {
						return (Class<T>) Object.class;
					}
				}

				fail(target, typeName);
			}
			currentClass = currentClass.getSuperclass();
			if (currentClass == null) {
				fail(target, typeName);
			}
		}
	}

	/**
	 * 在接口中查找泛型类型
	 *
	 * @param target   要查找泛型类型的对象
	 * @param clazz    要查找泛型的类
	 * @param typeName 查找的泛型字段名
	 * @param <T>      类类型
	 * @return 类
	 */
	@SuppressWarnings("unchecked")
	private static <T> Class<T> findGenericTypeInInterface0(
			final Class<?> target, Class<?> clazz, String typeName) {
		Type[] interfaces = target.getGenericInterfaces();
		for (Type interfaceType : interfaces) {
			if (interfaceType instanceof Class) {
				continue;
			}
			ParameterizedType paramType = (ParameterizedType) interfaceType;
			if (paramType.getRawType() != clazz) {
				continue;
			}
			Class<?> rawType = (Class<?>) paramType.getRawType();
			TypeVariable<? extends Class<?>>[] variables = rawType.getTypeParameters();
			int index = -1;
			for (int i = 0; i < variables.length; i++) {
				if (variables[i].getName().equals(typeName)) {
					index = i;
					break;
				}
			}
			if (index < 0) {
				return null;
			}
			Type argument = paramType.getActualTypeArguments()[index];
			if (argument instanceof Class) {
				return (Class<T>) argument;
			} else if (argument instanceof TypeVariable) {
				TypeVariable<?> variable = (TypeVariable<?>) argument;
				Type[] bounds = variable.getBounds();
				if (bounds.length == 1 && bounds[0] instanceof Class) {
					return (Class<T>) bounds[0];
				} else {
					// TODO 该泛型实际继承自多个接口
				}
			}
		}
		return null;
	}

	/**
	 * 查找失败
	 *
	 * @param type  类文件
	 * @param param 参数名
	 * @throws IllegalStateException 抛出异常，并且不会返回任何内容
	 */
	private static void fail(Class<?> type, String param) {
		throw new IllegalStateException(
				"泛型类型参数查找失败 '" + param + "': " + type);
	}
}