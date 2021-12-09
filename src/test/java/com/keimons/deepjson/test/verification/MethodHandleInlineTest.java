package com.keimons.deepjson.test.verification;

import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.util.MethodHandleUtil;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * {@link MethodHandle}参数折叠验证
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class MethodHandleInlineTest {

	private int value;

	@Test
	public void testBound() throws Throwable {
		Node node = new Node();
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();

		// 不绑定参数
		MethodHandle handle = lookup.findSetter(Node.class, "value", int.class);
		handle.invoke(node, 1);
		assert node.value == 1; // true

		// 绑定参数 第0个参数绑定为node
		MethodHandle newHandle = MethodHandles.insertArguments(handle, 0, node);
		newHandle.invoke(2);
		assert node.value == 2; // true
	}

	@Test
	public void testAdd() throws Throwable {
		Node node = new Node();
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();

		// 未加参数
		MethodHandle handle = lookup.findSetter(Node.class, "value", int.class);
		handle.invoke(node, 1);
		assert node.value == 1; // true

		// 添加参数 第0个参数前添加参数，添加的参数没有任何用处
		MethodHandle newHandle = MethodHandles.dropArguments(handle, 0, Node.class);
		newHandle.invoke((Node) null, node, 2);
		assert node.value == 2; // true
	}

	@Test
	public void testFold() throws Throwable {
		Node node = new Node();
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();

		// 未加参数
		MethodHandle handle = lookup.findSetter(Node.class, "value", int.class);
		handle.invoke(node, 1);
		assert node.value == 1; // true

		MethodHandle max = lookup.findStatic(Math.class, "max", MethodType.methodType(int.class, int.class, int.class));
		// 添加2个int参数
		handle = MethodHandles.dropArguments(handle, 2, int.class, int.class);
		// 使用max句柄的返回值替换第一个参数
		handle = MethodHandles.foldArguments(handle, 1, max);
		handle.invoke(node, 2, 3);
		assert node.value == 3; // true
		// @Hidden
		// @Compiled
		// @ForceInline
		// static void reinvoke_035(Object var0, Object var1, int var2, int var3) {
		//     Species_LL var7;
		//     Object var4 = (var7 = (Species_LL)var0).argL1;
		//     int var5 = ((MethodHandle)var4).invokeBasic(var2, var3);
		//     Object var6 = var7.argL0;
		//     ((MethodHandle)var6).invokeBasic(var1, var5);
		// }
	}

	@Test
	public void test() throws Throwable {
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();

		Class<?> clazz = this.getClass();

		MethodType mt = MethodType.methodType(void.class, JsonWriter.class, int.class);
		MethodHandle write = lookup.findStatic(clazz, "write", mt);
		MethodHandle getter = lookup.findGetter(clazz, "value", int.class);
		// 插入一个新的参数在参数列表的最后
		write = MethodHandles.dropArguments(write, 2, clazz);
		System.out.println(write.type());
		// 将第一个参数折叠为class的getter方法
		write = MethodHandles.foldArguments(write, 1, getter);
		System.out.println(write.type());
		// 测试
		MethodHandleInlineTest test = new MethodHandleInlineTest();
		test.value = 1024;
		write.invoke((JsonWriter) null, test);
	}

	public static void write(JsonWriter writer, int value) {
		System.out.println("测试打印值：" + value);
	}

	public static class Node {

		private int value;

		public int sum(int a, int b) {
			return a + b;
		}
	}
}