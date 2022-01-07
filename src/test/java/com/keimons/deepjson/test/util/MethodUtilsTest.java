package com.keimons.deepjson.test.util;

import com.keimons.deepjson.internal.util.MethodUtils;
import com.keimons.deepjson.test.AssertUtil;
import com.keimons.deepjson.test.closed.ClosedNode;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;

/**
 * {@link MethodUtils}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MethodUtilsTest {

	@Test
	public void testRTMethodDescriptor() {
		System.err.println(this.getClass().getModule());
		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(boolean.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(boolean.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(char.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(char.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(byte.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(byte.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(short.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(short.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(int.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(int.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(long.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(long.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(float.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(float.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(double.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(double.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(Object.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(String.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(String.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(Void.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(Void.class)
		);
	}

	@Test
	public void testPTMethodDescriptor() {
		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, boolean.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, boolean.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, char.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, char.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, byte.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, byte.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, short.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, short.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, int.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, int.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, long.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, long.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, float.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, float.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, double.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, double.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, Object.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, Object.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(void.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(void.class, boolean.class, char.class,
						byte.class, short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				)
		);
	}

	@Test
	public void testRPMethodDescriptor() {
		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(boolean.class, boolean.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(boolean.class, boolean.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(char.class, char.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(char.class, char.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(byte.class, byte.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(byte.class, byte.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(short.class, short.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(short.class, short.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(int.class, int.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(int.class, int.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(long.class, long.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(long.class, long.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(float.class, float.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(float.class, float.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(double.class, double.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(double.class, double.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, Object.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(Object.class, Object.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(String.class, Object.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(String.class, Object.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, String.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(Object.class, String.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(String.class, int.class, Object.class, int.class).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(String.class, int.class, Object.class, int.class)
		);

		AssertUtil.assertEquals("方法工具类测试",
				MethodType.methodType(Object.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class,
						Object.class, String.class, boolean.class, char.class, byte.class,
						short.class, int.class, long.class, float.class, double.class
				).toMethodDescriptorString(),
				MethodUtils.getMethodDescriptor(Object.class, boolean.class, char.class,
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
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 0);

		constructor = ClosedNode.class.getDeclaredConstructor(boolean.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value0", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(char.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value1", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(byte.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value2", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(short.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value3", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(int.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value4", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(long.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value5", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(float.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value6", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(double.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "value7", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(String.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "name", names[0]);

		constructor = ClosedNode.class.getDeclaredConstructor(String.class, int.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 2);
		AssertUtil.assertEquals("方法工具类测试", "name", names[0]);
		AssertUtil.assertEquals("方法工具类测试", "level", names[1]);
	}

	@Test
	public void testInnerClassConstructorParameterNames() throws Exception {
		Constructor<ClosedNode.InnerClass0> constructor;
		String[] names;
		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "this$0", names[0]);

		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class, String.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 2);
		AssertUtil.assertEquals("方法工具类测试", "this$0", names[0]);
		AssertUtil.assertEquals("方法工具类测试", "name", names[1]);

		constructor = ClosedNode.InnerClass0.class.getDeclaredConstructor(ClosedNode.class, Object.class, String.class, Object.class, String.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 5);
		AssertUtil.assertEquals("方法工具类测试", "this$0", names[0]);
		AssertUtil.assertEquals("方法工具类测试", "value0", names[1]);
		AssertUtil.assertEquals("方法工具类测试", "value1", names[2]);
		AssertUtil.assertEquals("方法工具类测试", "value2", names[3]);
		AssertUtil.assertEquals("方法工具类测试", "value3", names[4]);
	}

	@Test
	public void testStaticInnerClassConstructorParameterNames() throws Exception {
		Constructor<ClosedNode.InnerClass1> constructor;
		String[] names;
		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor();
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 0);

		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor(String.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 1);
		AssertUtil.assertEquals("方法工具类测试", "name", names[0]);

		constructor = ClosedNode.InnerClass1.class.getDeclaredConstructor(Object.class, String.class, Object.class, String.class);
		names = MethodUtils.getConstructorParameterNames(constructor);
		AssertUtil.assertTrue("方法工具类测试", names.length == 4);
		AssertUtil.assertEquals("方法工具类测试", "value0", names[0]);
		AssertUtil.assertEquals("方法工具类测试", "value1", names[1]);
		AssertUtil.assertEquals("方法工具类测试", "value2", names[2]);
		AssertUtil.assertEquals("方法工具类测试", "value3", names[3]);
	}
}