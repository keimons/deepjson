package com.keimons.deepjson.test.util;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.ConstructorOptions;
import com.keimons.deepjson.annotation.CodecCreator;
import com.keimons.deepjson.internal.util.ConstructorUtils;
import com.keimons.deepjson.internal.util.InternalIgnorableException;
import com.keimons.deepjson.test.AssertUtils;
import com.keimons.deepjson.test.closed.ClosedNode;
import com.keimons.deepjson.util.IllegalAnnotationException;
import com.keimons.deepjson.util.InferenceFailedException;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * {@link ConstructorUtils}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ConstructorUtilsTest {

	@Test
	public void testRTMethodDescriptor() {
		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(boolean.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(boolean.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(char.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(char.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(byte.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(byte.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(short.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(short.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(int.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(int.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(long.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(long.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(float.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(float.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(double.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(double.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(Object.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(String.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(String.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(Void.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(Void.class)
		);
	}

	@Test
	public void testPTMethodDescriptor() {
		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, boolean.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, boolean.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, char.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, char.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, byte.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, byte.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, short.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, short.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, int.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, int.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, long.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, long.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, float.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, float.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, double.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, double.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, Object.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, Object.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(void.class, boolean.class, char.class,
						byte.class, short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				)
		);
	}

	@Test
	public void testRPMethodDescriptor() {
		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(boolean.class, boolean.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(boolean.class, boolean.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(char.class, char.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(char.class, char.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(byte.class, byte.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(byte.class, byte.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(short.class, short.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(short.class, short.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(int.class, int.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(int.class, int.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(long.class, long.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(long.class, long.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(float.class, float.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(float.class, float.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(double.class, double.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(double.class, double.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, Object.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(Object.class, Object.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(String.class, Object.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(String.class, Object.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, String.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(Object.class, String.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(String.class, int.class, Object.class, int.class).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(String.class, int.class, Object.class, int.class)
		);

		AssertUtils.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				).toMethodDescriptorString(),
				ConstructorUtils.getConstructorDescriptor(Object.class, boolean.class, char.class,
						byte.class, short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				)
		);
	}

	@Test
	public void testConstructorParameterNames() throws Exception {
		Constructor<ClosedNode> constructor;
		String[] names;

		constructor = ClosedNode.class.getDeclaredConstructor();
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 0);

		constructor = ClosedNode.class.getDeclaredConstructor(boolean.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value0", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(char.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value1", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(byte.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value2", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(short.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value3", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(int.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value4", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(long.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value5", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(float.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value6", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(double.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "value7", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(String.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "name", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(String.class, int.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 2);
		AssertUtils.assertEquals("方法工具类测试", "name", names[0]);
		AssertUtils.assertEquals("方法工具类测试", "level", names[1]);
	}

	@Test
	public void testInnerClassConstructorParameterNames() throws Exception {
		Constructor<ClosedNode.InnerClass0> constructor;
		String[] names;
		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "this$0", names[0]);

		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class, String.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 2);
		AssertUtils.assertEquals("方法工具类测试", "this$0", names[0]);
		AssertUtils.assertEquals("方法工具类测试", "name", names[1]);

		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class, Object.class, String.class, Object.class, String.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 5);
		AssertUtils.assertEquals("方法工具类测试", "this$0", names[0]);
		AssertUtils.assertEquals("方法工具类测试", "value0", names[1]);
		AssertUtils.assertEquals("方法工具类测试", "value1", names[2]);
		AssertUtils.assertEquals("方法工具类测试", "value2", names[3]);
		AssertUtils.assertEquals("方法工具类测试", "value3", names[4]);
	}

	@Test
	public void testStaticInnerClassConstructorParameterNames() throws Exception {
		Constructor<ClosedNode.InnerClass1> constructor;
		String[] names;
		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor();
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 0);

		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor(String.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 1);
		AssertUtils.assertEquals("方法工具类测试", "name", names[0]);

		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor(Object.class, String.class, Object.class, String.class);
		names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertTrue("方法工具类测试", names.length == 4);
		AssertUtils.assertEquals("方法工具类测试", "value0", names[0]);
		AssertUtils.assertEquals("方法工具类测试", "value1", names[1]);
		AssertUtils.assertEquals("方法工具类测试", "value2", names[2]);
		AssertUtils.assertEquals("方法工具类测试", "value3", names[3]);
	}

	@Test
	public void testConstructor0() throws InternalIgnorableException {
		Constructor<?> constructor = ConstructorUtils.getConstructorWithAnnotation(Node0.class);
		assert constructor != null;
		String[] names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertEquals("构造函数参数名称", "name", names[0]);
		AssertUtils.assertEquals("构造函数参数名称", "age", names[1]);
	}

	@Test
	public void testConstructor1() throws InternalIgnorableException {
		Constructor<?> constructor = ConstructorUtils.getConstructorWithAnnotation(Node1.class);
		assert constructor != null;
		String[] names = ConstructorUtils.getConstructorParameterNames(constructor);
		AssertUtils.assertEquals("构造函数参数名称", "value0", names[0]);
		AssertUtils.assertEquals("构造函数参数名称", "value1", names[1]);
	}

	@Test
	public void testConstructorException0() {
		try {
			ConstructorUtils.getConstructorWithAnnotation(Node2.class);
			System.err.println("测试失败");
		} catch (Exception e) {
			AssertUtils.assertEquals("构造函数参数名称", IllegalAnnotationException.class, e.getClass());
		}
	}

	@Test
	public void testConstructorException1() {
		try {
			Constructor<?> constructor = ConstructorUtils.getConstructorWithAnnotation(Node3.class);
			assert constructor != null;
			String[] names = ConstructorUtils.getConstructorParameterNames(constructor);
			System.err.println("测试失败：" + Arrays.toString(names));
		} catch (Exception e) {
			AssertUtils.assertEquals("构造函数参数名称", IllegalAnnotationException.class, e.getClass());
		}
	}

	@Test
	public void testConstructorOptionsDisable() {
		try {
			CodecConfig.constructorSelector = ConstructorOptions.DISABLE;
			Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
			System.err.println("测试失败：" + constructor);
		} catch (Exception e) {
			AssertUtils.assertEquals("禁用构造函数自动查找", InferenceFailedException.class, e.getClass());
		}
	}

	@Test
	public void testConstructorOptionsRandom() {
		CodecConfig.constructorSelector = ConstructorOptions.RANDOM;
		Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
		AssertUtils.assertNotNull("随机使用构造函数自动查找", constructor);
	}

	@Test
	public void testConstructorOptionsFirst() {
		CodecConfig.constructorSelector = ConstructorOptions.FIRST;
		Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
		AssertUtils.assertTrue("使用第一个构造函数自动查找", constructor.getParameterCount() == 2);
		AssertUtils.assertEquals("使用第一个构造函数自动查找", String.class, constructor.getParameterTypes()[0]);
		AssertUtils.assertEquals("使用第一个构造函数自动查找", String.class, constructor.getParameterTypes()[1]);
	}

	@Test
	public void testConstructorOptionsLast() {
		CodecConfig.constructorSelector = ConstructorOptions.LAST;
		Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
		AssertUtils.assertTrue("使用最后一个构造函数", constructor.getParameterCount() == 3);
		AssertUtils.assertEquals("使用最后一个构造函数", int.class, constructor.getParameterTypes()[0]);
		AssertUtils.assertEquals("使用最后一个构造函数", int.class, constructor.getParameterTypes()[1]);
		AssertUtils.assertEquals("使用最后一个构造函数", int.class, constructor.getParameterTypes()[2]);
	}

	@Test
	public void testConstructorOptionsLeast() {
		CodecConfig.constructorSelector = ConstructorOptions.LEAST;
		Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
		AssertUtils.assertTrue("使用参数最少的构造函数", constructor.getParameterCount() == 1);
		AssertUtils.assertEquals("使用参数最少的构造函数", String.class, constructor.getParameterTypes()[0]);
	}

	@Test
	public void testConstructorOptionsMost() {
		CodecConfig.constructorSelector = ConstructorOptions.MOST;
		Constructor<?> constructor = ConstructorUtils.findConstructor(Node4.class);
		AssertUtils.assertTrue("使用参数最多的构造函数", constructor.getParameterCount() == 3);
		AssertUtils.assertEquals("使用参数最多的构造函数", String.class, constructor.getParameterTypes()[0]);
		AssertUtils.assertEquals("使用参数最多的构造函数", String.class, constructor.getParameterTypes()[1]);
		AssertUtils.assertEquals("使用参数最多的构造函数", String.class, constructor.getParameterTypes()[2]);
	}

	static class Node0 {

		String name;

		int age;

		public Node0(String name) {
			this.name = name;
		}

		@CodecCreator
		public Node0(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}

	static class Node1 {

		String name;

		int age;

		public Node1(String name) {
			this.name = name;
		}

		@CodecCreator({"value0", "value1"})
		public Node1(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}

	static class Node2 {

		String name;

		int age;

		@CodecCreator()
		public Node2(String name) {
			this.name = name;
		}

		@CodecCreator({"value0", "value1"})
		public Node2(String name, int age) {
			this.name = name;
			this.age = age;
		}
	}

	static class Node3 {

		String name;

		int age;

		@CodecCreator({"name", "age"})
		public Node3(String name) {
			this.name = name;
		}
	}

	static class Node4 {

		public Node4(String value0, String value1) {

		}

		public Node4(String value0) {

		}

		public Node4(String value0, String value1, String value2) {

		}

		public Node4(int value0) {

		}

		public Node4(int value0, int value1, int value2) {

		}
	}
}