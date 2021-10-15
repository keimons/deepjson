package com.keimons.deepjson.test.codec.primitive;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Random;

/**
 * {@code Primitive}基础类型测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class PrimitiveTest {

	@Test
	public void testByte() {
		System.out.println("----------------> Byte Test <----------------");
		for (int i = -1000; i <= 1000; i++) {
			String json = DeepJson.toJsonString(i);
			try {
				byte value = DeepJson.parseObject(json, byte.class);
				assert i == value;
			} catch (NumberFormatException e) {
				if (Byte.MIN_VALUE <= i && i <= Byte.MAX_VALUE) {
					e.printStackTrace();
				}
				// ignore out of byte.
			}
		}
		System.out.println("codec successful of byte.");
		System.out.println("----------------> Byte Test <----------------");
	}

	@Test
	public void testShort() {
		System.out.println("----------------> Short Test <----------------");
		for (int i = -100000; i <= 100000; i++) {
			String json = DeepJson.toJsonString(i);
			try {
				short value = DeepJson.parseObject(json, short.class);
				assert i == value;
			} catch (NumberFormatException e) {
				if (Short.MIN_VALUE <= i && i <= Short.MIN_VALUE) {
					e.printStackTrace();
				}
				// ignore out of short.
			}
		}
		System.out.println("codec successful of short.");
		System.out.println("----------------> Short Test <----------------");
	}

	@Test
	public void testInt() {
		System.out.println("----------------> Integer Test <----------------");

		// check random
		Random random = new Random();
		for (long i = -100_0020_0000L; i <= 100_0020_0000L; i += random.nextInt(10_0000)) {
			String json = DeepJson.toJsonString(i);
			try {
				int value = DeepJson.parseObject(json, int.class);
				assert i == value;
			} catch (NumberFormatException e) {
				if (Integer.MIN_VALUE <= i && i <= Integer.MAX_VALUE) {
					e.printStackTrace();
				}
				// ignore out of int.
			}
		}

		// check min bound
		for (long i = Integer.MIN_VALUE - 10_0000L; i <= Integer.MIN_VALUE + 10_0000L; i++) {
			String json = DeepJson.toJsonString(i);
			try {
				int value = DeepJson.parseObject(json, int.class);
				assert i == value;
			} catch (NumberFormatException e) {
				if (Integer.MIN_VALUE <= i) {
					e.printStackTrace();
				}
				// ignore out of int.
			}
		}

		// check max bound
		for (long i = Integer.MAX_VALUE - 10_0000L; i <= Integer.MAX_VALUE + 10_0000L; i++) {
			String json = DeepJson.toJsonString(i);
			try {
				int value = DeepJson.parseObject(json, int.class);
				assert i == value;
			} catch (NumberFormatException e) {
				if (i <= Integer.MAX_VALUE) {
					e.printStackTrace();
				}
				// ignore out of int.
			}
		}

		System.out.println("codec successful of integer.");
		System.out.println("----------------> Integer Test <----------------");
	}

	@Test
	public void testLong() {
		System.out.println("----------------> Long Test <----------------");

		BigInteger MIN = new BigInteger(String.valueOf(Long.MIN_VALUE));
		BigInteger MAX = new BigInteger(String.valueOf(Long.MAX_VALUE));
		// check random
		Random random = new Random();
		BigInteger start = new BigInteger("-10000000000000000000");
		BigInteger limit = new BigInteger("10000000000000000000");
		for (; start.compareTo(limit) < 0; start = start.add(new BigInteger(String.valueOf(random.nextLong() >>> 6)))) {
			String json = DeepJson.toJsonString(start.toString());
			try {
				long value = DeepJson.parseObject(json, long.class);
				assert start.longValue() == value;
			} catch (NumberFormatException e) {
				if (MIN.compareTo(start) <= 0 && start.compareTo(MAX) <= 0) {
					e.printStackTrace();
				}
				// ignore out of long.
			}
		}

		BigInteger ADD = new BigInteger("1");


		// check min bound
		start = new BigInteger(String.valueOf(Long.MIN_VALUE)).add(new BigInteger("-100000"));
		limit = new BigInteger(String.valueOf(Long.MIN_VALUE)).add(new BigInteger("100000"));
		for (; start.compareTo(limit) < 0; start = start.add(ADD)) {
			String json = DeepJson.toJsonString(start.toString());
			try {
				long value = DeepJson.parseObject(json, long.class);
				assert start.longValue() == value;
			} catch (NumberFormatException e) {
				if (MIN.compareTo(start) <= 0 && start.compareTo(MAX) <= 0) {
					e.printStackTrace();
				}
				// ignore out of long.
			}
		}

		// check max bound
		start = new BigInteger(String.valueOf(Long.MAX_VALUE)).add(new BigInteger("-100000"));
		limit = new BigInteger(String.valueOf(Long.MAX_VALUE)).add(new BigInteger("100000"));
		for (; start.compareTo(limit) < 0; start = start.add(ADD)) {
			String json = DeepJson.toJsonString(start.toString());
			try {
				long value = DeepJson.parseObject(json, long.class);
				assert start.longValue() == value;
			} catch (NumberFormatException e) {
				if (MIN.compareTo(start) <= 0 && start.compareTo(MAX) <= 0) {
					e.printStackTrace();
				}
				// ignore out of long.
			}
		}

		System.out.println("codec successful of long.");
		System.out.println("----------------> Long Test <----------------");
	}
}