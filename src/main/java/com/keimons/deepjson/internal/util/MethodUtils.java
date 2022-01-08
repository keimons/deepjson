package com.keimons.deepjson.internal.util;

import com.keimons.deepjson.annotation.CodecCreator;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.IllegalAnnotationException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * 方法工具类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class MethodUtils {

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
	public static String getMethodDescriptor(Class<?> rt, Class<?>... pts) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Class<?> pt : pts) {
			appendMethodSign(pt, sb);
		}
		sb.append(')');
		appendMethodSign(rt, sb);
		return sb.toString();
	}

	private static void appendMethodSign(Class<?> clazz, StringBuilder sb) {
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
			String desc = getMethodDescriptor(void.class, constructor.getParameterTypes());
			byte[] buf = BytecodeUtils.findBytecodes(clazz);
			return BytecodeUtils.findConstructorParameterNames(buf, desc);
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