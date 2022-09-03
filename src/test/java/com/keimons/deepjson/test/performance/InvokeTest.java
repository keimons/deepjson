package com.keimons.deepjson.test.performance;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.Reference;

/**
 * InvokeTest
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class InvokeTest {

	@Test
	public void test() {
		/**
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			MethodHandle test = lookup.findStatic(InvokeTest.class, "test", MethodType.methodType(int.class, Object.class));
			MethodHandle test1 = lookup.findStatic(InvokeTest.class, "cast", MethodType.methodType(int.class, int.class));
			for (int i = 0; i < 100000; i++) {
				test1.invoke((int) test.invoke(new Object()));
			}


			Object value = 1024;
			int sum = 0;

			for (int i = 0; i < 10000; i++) {
				sum += (int) test.invokeExact(value);
				sum += (int) test.invoke(value);
			}

			long start = System.nanoTime();
			for (int i = 0; i < 1000_0000; i++) {
				sum += (int) test.invoke(value);
			}
			long finish = System.nanoTime();
			System.out.println("exact : " + (finish - start));

			sum = 0;

			start = System.nanoTime();
			for (int i = 0; i < 1000_0000; i++) {
				sum += (int) test.invokeExact(value);
			}
			finish = System.nanoTime();
			System.out.println("invoke: " + (finish - start));

			Reference.reachabilityFence(sum);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		*/
	}

	public static int cast(int value) {
		return value;
	}

	public static int test(Object value) {
		return 1;
	}
}
