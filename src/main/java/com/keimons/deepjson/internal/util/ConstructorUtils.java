package com.keimons.deepjson.internal.util;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.ConstructorOptions;
import com.keimons.deepjson.annotation.CodecCreator;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.IllegalAnnotationException;
import com.keimons.deepjson.util.InferenceFailedException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 构造方法工具类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class ConstructorUtils {

	/**
	 * 查找构造方法
	 * <p>
	 * 在一个类中查找可用的构造方法，查找优先级：
	 * <ul>
	 *     <li>标注了{@link CodecCreator}注解的构造方法。</li>
	 *     <li>无参构造方法</li>
	 *     <li>{@link ConstructorOptions}构造器选项指定的构造方法</li>
	 * </ul>
	 * 如果类中存在多个有参构造方法，则抛出异常
	 *
	 * @param clazz 要查找构造方法的类
	 * @return 构造方法
	 * @throws IllegalAnnotationException 类中包含多个{@link CodecCreator}标注的构造方法
	 * @throws InferenceFailedException   构造方法查找失败
	 * @see ConstructorOptions 构造器选项
	 */
	public static Constructor<?> findConstructor(Class<?> clazz) {
		if (clazz.isPrimitive() || clazz.isInterface() || clazz.isArray()) {
			throw new InferenceFailedException("constructor not found in " + clazz);
		}
		Constructor<?> constructor = getConstructorWithAnnotation(clazz);
		if (constructor != null) {
			return constructor;
		}
		try {
			return clazz.getDeclaredConstructor();
		} catch (NoSuchMethodException e) {
			// ignore exception
			// disable
			if (CodecConfig.constructorSelector == ConstructorOptions.DISABLE) {
				ConstructorOptions.newInferenceFailedException(clazz);
			}
			List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
			try {
				byte[] buf = BytecodeUtils.findBytecodes(clazz);
				BytecodeUtils.ConstructorCollector collector = new BytecodeUtils.ConstructorCollector();
				BytecodeUtils.findConstructorParameterNames(buf, collector);
				List<String> mts = collector.mts;
				for (int i = 0; i < mts.size(); i++) {
					Class<?>[] pts = parseConstructorDescriptor(mts.get(i), clazz.getClassLoader());
					constructors.add(clazz.getDeclaredConstructor(pts));
				}
			} catch (Throwable ex) {
				ConstructorOptions.newInferenceFailedException(clazz, ex);
			}
			if (constructors.size() == 0) {
				ConstructorOptions.newInferenceFailedException(clazz);
			}
			return CodecConfig.constructorSelector.select(constructors.toArray(new Constructor[0]));
		}
	}

	/**
	 * 构造方法描述符转化为参数列表
	 *
	 * @param sign   构造方法描述符
	 * @param loader 类加载器
	 * @return 参数列表
	 */
	public static Class<?>[] parseConstructorDescriptor(String sign, ClassLoader loader) {
		if (!sign.startsWith("(") || !sign.endsWith(")V")) {
			throw new IllegalArgumentException("not a constructor descriptor: " + sign);
		}
		ArrayList<Class<?>> pts = new ArrayList<Class<?>>();
		for (int i = 1, length = sign.length() - 2; i < length; i++) {
			char charType = sign.charAt(i);
			Class<?> clazz = ClassUtil.basicType(charType);
			if (clazz == null) {
				String className = sign.substring(i, sign.indexOf(";", i + 1));
				i += className.length();
				clazz = parseSign(className, 0, loader);
			}
			if (clazz == void.class || clazz == null) {
				throw new IllegalArgumentException("bad signature type: " + sign);
			}
			pts.add(clazz);
		}
		return pts.toArray(new Class<?>[0]);
	}

	/**
	 * 类型描述符转化为类型
	 *
	 * @param str    类型描述
	 * @param index  查找开始位置
	 * @param loader 类加载器
	 * @return 类型
	 */
	private static Class<?> parseSign(String str, int index, ClassLoader loader) {
		char sign = str.charAt(index);
		if (sign == 'L') {
			String name = str.substring(index + 1).replace('/', '.');
			try {
				return (loader == null)
						? Class.forName(name, false, null)
						: loader.loadClass(name);
			} catch (ClassNotFoundException ex) {
				throw new TypeNotPresentException(name, ex);
			}
		} else if (sign == '[') {
			Class<?> clazz = parseSign(str, ++index, loader);
			if (clazz != null) {
				clazz = Array.newInstance(clazz, 0).getClass();
			}
			return clazz;
		} else {
			return null;
		}
	}

	/**
	 * 获取方法描述符，例如：
	 * <ul>
	 *     <li>构造方法：{@code ()V}</li>
	 *     <li>构造方法：{@code (Ljava.lang.String;)V}</li>
	 *     <li>构造方法：{@code (I)V}</li>
	 *     <li>构造方法：{@code (IZ)V}</li>
	 *     <li>普通方法：{@code (Ljava.lang.String;)Ljava.lang.Object;}</li>
	 *     <li>普通方法：{@code ()I}</li>
	 *     <li>普通方法：{@code (II)I}</li>
	 * </ul>
	 *
	 * @param rt  返回类型
	 * @param pts 参数类型
	 * @return 返回值
	 */
	public static String getConstructorDescriptor(Class<?> rt, Class<?>... pts) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Class<?> pt : pts) {
			appendSign(pt, sb);
		}
		sb.append(')');
		appendSign(rt, sb);
		return sb.toString();
	}

	private static void appendSign(Class<?> clazz, StringBuilder sb) {
		char c = ClassUtil.basicType(clazz);
		if (c != 'L') {
			sb.append(c);
		} else {
			boolean notArray = (!clazz.isArray());
			if (notArray) {
				sb.append('L');
			}
			sb.append(clazz.getName().replace('.', '/'));
			if (notArray) {
				sb.append(';');
			}
		}
	}

	/**
	 * 获取构造方法参数名
	 * <p>
	 * 对于类和静态内部类，能获取到构造方法的参数名。
	 * 对于内部类，第一个参数会是{@code this$0}，指向父对象。
	 *
	 * @param constructor 方法构造器
	 * @return 参数名
	 * @throws InternalIgnorableException 查找构造方法的参数名时的异常
	 * @throws IllegalAnnotationException 属性名称数量不符
	 */
	public static @Nullable String[] getConstructorParameterNames(Constructor<?> constructor) throws InternalIgnorableException {
		CodecCreator annotation = constructor.getAnnotation(CodecCreator.class);
		if (annotation == null || annotation.value() == null || annotation.value().length == 0) {
			Class<?> clazz = constructor.getDeclaringClass();
			String desc = getConstructorDescriptor(void.class, constructor.getParameterTypes());
			byte[] buf = BytecodeUtils.findBytecodes(clazz);
			BytecodeUtils.NamesCollector collector = new BytecodeUtils.NamesCollector(desc);
			BytecodeUtils.findConstructorParameterNames(buf, collector);
			return collector.names;
		} else {
			if (annotation.value().length == constructor.getParameterTypes().length) {
				return annotation.value();
			}
			throw new IllegalAnnotationException(
					"number of incompatible parameters, class: " + constructor.getDeclaringClass() +
							", constructor: " + constructor +
							", names: " + Arrays.toString(annotation.value())
			);
		}
	}

	/**
	 * 获取{@link CodecCreator}标注的构造方法
	 *
	 * @param clazz 要获取构造方法的类
	 * @return {@link CodecCreator}标注的构造方法
	 * @throws IllegalAnnotationException 类中包含多个{@link CodecCreator}标注的构造方法
	 */
	public static @Nullable Constructor<?> getConstructorWithAnnotation(Class<?> clazz) {
		Constructor<?> result = null;
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			CodecCreator ann = constructor.getAnnotation(CodecCreator.class);
			if (ann != null) {
				if (result == null) {
					result = constructor;
				} else {
					throw new IllegalAnnotationException("more than one @CodecCreator in class: " + clazz);
				}
			}
		}
		return result;
	}
}