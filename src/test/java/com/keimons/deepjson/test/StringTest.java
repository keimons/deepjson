package com.keimons.deepjson.test;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;

public class StringTest {

	@Test
	public void test() throws NoSuchFieldException, IllegalAccessException {
		MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(String.class, MethodHandles.lookup());
		VarHandle value1 = lookup.findVarHandle(String.class, "value", byte[].class);
		VarHandle value2 = lookup.findVarHandle(String.class, "coder", byte.class);
		System.out.println(Arrays.toString((byte[]) value1.get("我1们a啊")));
		System.out.println(Arrays.toString(("我1们a啊").getBytes()));
		char[] c = {'\uD83D', '\uDE05'};
		String str = new String(c);

		"\uD83D\uDE05".codePoints().forEach(System.out::println);
		System.out.println(str);
		System.out.println((byte) value2.get(str));
	}
}