package com.keimons.deepjson.test.verification;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * {@link MethodHandle}性能验证
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MethodHandleJavacTest {

	private int value;

	@Test
	public void test() throws Throwable {
		MethodHandle value = MethodHandles.lookup().findSetter(MethodHandleJavacTest.class, "value", int.class);
		MethodHandle getter = MethodHandles.lookup().findGetter(MethodHandleJavacTest.class, "value", int.class);
		MethodHandleJavacTest obj = new MethodHandleJavacTest();
		for (int i = 0; i < 100000000; i++) {
			// INVOKEVIRTUAL java/lang/invoke/MethodHandle.invoke (Lcom/keimons/deepjson/test/verification/MethodHandleTest;I)V
			value.invoke(obj, i);
		}
		for (int i = 0; i < 10000; i++) {
			// NEWARRAY T_INT
			test1(i);
		}
		for (int i = 0; i < 10000; i++) {
			// ANEWARRAY java/lang/Object
			// INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
			test2(i);
		}
		System.out.println((int) getter.invoke(this));
	}

	public void test1(int... values) {
		value += values.hashCode();
	}

	public void test2(Object... values) {
		value += values.hashCode();
	}
}