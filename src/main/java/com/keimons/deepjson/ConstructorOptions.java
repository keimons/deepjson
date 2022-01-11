package com.keimons.deepjson;

import com.keimons.deepjson.annotation.CodecCreator;
import com.keimons.deepjson.internal.ConstructorSelector;
import com.keimons.deepjson.util.InferenceFailedException;

import java.lang.reflect.Constructor;
import java.util.Random;

/**
 * 构造器选项
 * <p>
 * 当类中没有无参构造器或{@link CodecCreator}指定的构造器时，可以通过全局配置，选择一个构造器使用。
 * 受支持的构造器选项有：
 * <ul>
 *     <li>{@link #DISABLE} 禁用此功能。</li>
 *     <li>{@link #RANDOM} 随机一个构造器使用。</li>
 *     <li>{@link #FIRST} 类中定义的第一个构造器。</li>
 *     <li>{@link #LAST} 类中定义的最后一个构造器。</li>
 *     <li>{@link #LEAST} 参数最少的构造器（查找到多个构造器时，返回类中定义的第一个）。</li>
 *     <li>{@link #MOST} 参数最多的构造器（查找到多个构造器时，返回类中定义的第一个）。</li>
 * </ul>
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see InferenceFailedException 没有默认构造器异常
 * @since 1.6
 **/
public enum ConstructorOptions implements ConstructorSelector {

	/**
	 * 禁用
	 */
	DISABLE {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			newInferenceFailedException(constructors[0].getDeclaringClass());
			return null;
		}
	},

	/**
	 * 在构造器中随机一个
	 */
	RANDOM {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			return constructors[random.nextInt(constructors.length)];
		}
	},

	/**
	 * 按照类中定义的顺序，使用第一个
	 */
	FIRST {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			return constructors[0];
		}
	},

	/**
	 * 按照类中定义的顺序，使用最后一个
	 */
	LAST {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			return constructors[constructors.length - 1];
		}
	},

	/**
	 * 参数最少的构造器（查找到多个构造器时，按照类中定义顺序，使用第一个）
	 */
	LEAST {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			Constructor<?> constructor = constructors[0];
			for (int i = 1, length = constructors.length; i < length; i++) {
				if (constructor.getParameterCount() > constructors[i].getParameterCount()) {
					constructor = constructors[i];
				}
			}
			return constructor;
		}
	},

	/**
	 * 参数最多的构造器（查找到多个构造器时，按照类中定义顺序，使用第一个）
	 */
	MOST {
		@Override
		public Constructor<?> select(Constructor<?>[] constructors) {
			Constructor<?> constructor = constructors[0];
			for (int i = 1, length = constructors.length; i < length; i++) {
				if (constructor.getParameterCount() < constructors[i].getParameterCount()) {
					constructor = constructors[i];
				}
			}
			return constructor;
		}
	};

	private static final Random random = new Random();

	public static void newInferenceFailedException(Class<?> clazz) {
		throw new InferenceFailedException("default constructor not found in class: " + clazz.getName());
	}

	public static void newInferenceFailedException(Class<?> clazz, Throwable cause) {
		throw new InferenceFailedException("default constructor not found in class: " + clazz.getName(), cause);
	}
}