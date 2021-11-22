package com.keimons.deepjson.test.verification;

import com.keimons.deepjson.WriterBuffer;
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
	public void test() throws Throwable {
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();

		Class<?> clazz = this.getClass();

		MethodType mt = MethodType.methodType(void.class, WriterBuffer.class, int.class);
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
		write.invoke((WriterBuffer) null, test);
	}

	public static void write(WriterBuffer buffer, int value) {
		System.out.println("测试打印值：" + value);
	}
}