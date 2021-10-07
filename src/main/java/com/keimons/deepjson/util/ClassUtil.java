package com.keimons.deepjson.util;

import com.keimons.deepjson.Config;
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

	/**
	 * 查找一个类型的真实类型
	 * <p>
	 * 对于一个类型而言，共计有五种。
	 * <ul>
	 *     <li>
	 *         {@link Class}基本类型(raw type)，直接返回。
	 *     </li>
	 *     <li>
	 *         {@link ParameterizedType}参数化类型，返回RawType。
	 *     </li>
	 *     <li>
	 *         {@link TypeVariable}类型变量，在上下文环境中查找这个类型变量的真实类型。如果获取到的依然是
	 *         泛型参数，则使用该参数的边界{@link TypeVariable#getBounds()}。
	 *     </li>
	 *     <li>
	 *         {@link GenericArrayType}泛型数组，首先解析组件类型，然后递归获取真实类型。
	 *     </li>
	 *     <li>
	 *         {@link WildcardType}通配符类型，优先使用下界通配符，如果没有下界通配符，使用上界通配符时，
	 *         需要考虑多类型兼容问题。
	 *     </li>
	 * </ul>
	 *
	 * @param types       上下文环境
	 * @param writerIndex 栈顶位置
	 * @param type        要查找的类型
	 * @param excepted
	 * @return 真实类型
	 * @see #findGenericType(Type, Class, String) 查找泛型参数的真实类型
	 */
	public static Class<?> findClass(Type[] types, int writerIndex, Type type, Class<?> excepted) {
		// 普通类型
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		// 参数类型
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}
		// 泛型参数
		if (type instanceof TypeVariable) {
			TypeVariable<?> variable = (TypeVariable<?>) type;
			Class<?> clazz = (Class<?>) variable.getGenericDeclaration();
			String name = variable.getName();
			Type genericType = findGenericType(types, writerIndex, clazz, name);
			if (genericType == null) {
				genericType = variable;
			}
			// 没能查找到真正的Class类型，反而是一个泛型参数
			if (genericType instanceof TypeVariable) {
				Type[] bounds = ((TypeVariable<?>) genericType).getBounds();
				if (bounds.length == 1 || check(bounds)) {
					return findClass(types, writerIndex, bounds[0], excepted);
				} else {
					// 预期对象自描述类型，需要判断泛型参数是否能兼容所有边界类型
					if (excepted == null) {
						Type def = Config.getType(bounds);
						if (def == null) {
							throw new TypeNotFoundException("unknown type " + type.getTypeName());
						}
						return findClass(types, writerIndex, def, null);
					} else {
						for (Type bound : bounds) {
							Class<?> cls;
							if (bound instanceof ParameterizedType) {
								cls = (Class<?>) ((ParameterizedType) bound).getRawType();
							} else {
								cls = (Class<?>) bound;
							}
							if (!cls.isAssignableFrom(excepted)) {
								throw new TypeNotFoundException("unknown type " + type.getTypeName());
							}
						}
						return excepted;
					}
				}
			} else {
				return findClass(types, writerIndex, genericType, excepted);
			}
		}
		// 泛型数组
		if (type instanceof GenericArrayType) {
			GenericArrayType arrayType = (GenericArrayType) type;
			Class<?> clazz = findClass(types, writerIndex, arrayType.getGenericComponentType(), excepted);
			return Array.newInstance(clazz, 0).getClass();
		}
		// 通配类型
		if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			// 下界通配符 增强包容性，如果包含下界通配符，直接返回下界通配符。
			Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds.length > 0) {
				return findClass(types, writerIndex, lowerBounds[0], excepted);
			}
			// 上界通配符 如果包含上界通配符，尝试使用上界通配符。
			// 使用上界通配符时，可能有多个上界通配符，所以实际上有可能造成解码失败。
			// 期望对象自描述类型，但是如果没有，则有可能造成类型不兼容。
			Type[] upperBounds = wildcardType.getUpperBounds();
			if (upperBounds.length > 0) {
				return findClass(types, writerIndex, upperBounds[0], excepted);
			}
			// 无法解析 上下界均为空
			throw new TypeNotFoundException("unknown wildcard type " + type.getTypeName());
		}
		throw new TypeNotFoundException("unknown type " + type.getTypeName());
	}

	/**
	 * 检测类型
	 *
	 * @param bounds 边界信息
	 * @return {@code true}检测成功，{@code false}检测失败
	 */
	public static boolean check(Type[] bounds) {
		Class<?> parent;
		if (bounds[0] instanceof Class) {
			parent = (Class<?>) bounds[0];
		} else {
			parent = (Class<?>) ((ParameterizedType) bounds[0]).getRawType();
		}
		boolean check = true;
		for (int i = 1; i < bounds.length; i++) {
			Class<?> clazz = (Class<?>) bounds[i];
			if (!clazz.isAssignableFrom(parent)) {
				check = false;
				break;
			}
		}
		return check;
	}

	/**
	 * 在类中查找泛型类型的实际类型（仅作一层解析）
	 * <p>
	 * 多层解析如果当前类型中无法解析，继续向上查找，直到能解析出来为止。
	 *
	 * @param types       查找起始位置{@link Class}或{@link ParameterizedType}。
	 * @param readerIndex 开始读取位置
	 * @param target      查找目标
	 * @param name        类型变量，类型变量应该是{@link Class}、{@link TypeVariable}、
	 *                    {@link ParameterizedType}、{@link GenericArrayType}或者
	 *                    {@link WildcardType}中的一个。
	 * @return {@link Type}泛型类型，{@link TypeVariable}类型变量，{@code null}查找失败。
	 * <ul>
	 *     <li>
	 *         {@link Class}             基本类型(raw type)是一个普通的类。
	 *         {@link TypeVariable}      类型变量，通过边界判断是否合法。
	 *         {@link ParameterizedType} 参数化类型，需要进一步解析。
	 *         {@link GenericArrayType}  泛型数组，需要进一步解析。
	 *         {@link WildcardType}      通配符，可进一步解析为以上四个。
	 *     </li>
	 * </ul>
	 */
	public static @Nullable Type findGenericType(Type[] types, int readerIndex, Class<?> target, String name) {
		Type result = null;
		for (int i = readerIndex - 1; i >= 0; i--) {
			Type tmp = findGenericType(types[i], target, name);
			if (tmp == null) { // 查找中断
				return result;
			} else {
				result = tmp;
			}
			// 依然是泛型 继续向上查找
			if (result instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) result;
				name = tv.getName();
				target = (Class<?>) tv.getGenericDeclaration();
				continue;
			}
			return result;
		}
		// 处理边界问题，例如：T extends Number，实际应该返回Number类型。
		return result;
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
	 *     <li>
	 *         {@link ParameterizedType} 依然是带有泛型的子类型，例如：{@code HashMap<String, Integer>}。
	 *     </li>
	 *     <li>
	 *         {@link TypeVariable}      依然是泛型，例如：{@link Map}中的{@code K}，对应{@link Object}类型
	 *         	                         或{@link TypeVariable#getBounds()}。
	 *     </li>
	 *     <li>
	 *         {@link Class}             内部对象类型
	 *     </li>
	 *     <li>
	 *         {@code null}              查找失败
	 *     </li>
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
			if (type == target) {
				TypeVariable<? extends Class<?>>[] variables = clazz.getTypeParameters();
				for (TypeVariable<? extends Class<?>> variable : variables) {
					if (variable.getTypeName().equals(name)) {
						return variable;
					}
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