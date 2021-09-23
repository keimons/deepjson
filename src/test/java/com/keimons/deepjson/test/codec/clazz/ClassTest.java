package com.keimons.deepjson.test.codec.clazz;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@link Class}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassTest {

	@Test
	public void test() throws Exception {
		System.out.println("----------------> Class Test Encode <----------------");
		System.out.println("encode: " + DeepJson.toJsonString(void.class));

		System.out.println("encode: " + DeepJson.toJsonString(boolean.class));
		System.out.println("encode: " + DeepJson.toJsonString(byte.class));
		System.out.println("encode: " + DeepJson.toJsonString(short.class));
		System.out.println("encode: " + DeepJson.toJsonString(char.class));
		System.out.println("encode: " + DeepJson.toJsonString(int.class));
		System.out.println("encode: " + DeepJson.toJsonString(long.class));
		System.out.println("encode: " + DeepJson.toJsonString(float.class));
		System.out.println("encode: " + DeepJson.toJsonString(double.class));
		System.out.println("encode: " + DeepJson.toJsonString(Object.class));

		System.out.println("encode: " + DeepJson.toJsonString(boolean[].class));
		System.out.println("encode: " + DeepJson.toJsonString(byte[].class));
		System.out.println("encode: " + DeepJson.toJsonString(short[].class));
		System.out.println("encode: " + DeepJson.toJsonString(char[].class));
		System.out.println("encode: " + DeepJson.toJsonString(int[].class));
		System.out.println("encode: " + DeepJson.toJsonString(long[].class));
		System.out.println("encode: " + DeepJson.toJsonString(float[].class));
		System.out.println("encode: " + DeepJson.toJsonString(double[].class));
		System.out.println("encode: " + DeepJson.toJsonString(Object[].class));

		System.out.println("encode: " + DeepJson.toJsonString(boolean[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(byte[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(short[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(char[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(int[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(long[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(float[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(double[][].class));
		System.out.println("encode: " + DeepJson.toJsonString(Object[][].class));

		System.out.println("----------------> Class Test Decode <----------------");
		System.out.println("decode: " + DeepJson.parseObject("\"void\"", Class.class));

		System.out.println("decode: " + DeepJson.parseObject("\"boolean\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"byte\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"short\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"char\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"int\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"long\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"float\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"double\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"java.lang.Object\"", Class.class));

		System.out.println("decode: " + DeepJson.parseObject("\"[Z\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[B\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[S\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[C\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[I\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[J\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[F\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[D\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[Ljava.lang.Object;\"", Class.class));

		System.out.println("decode: " + DeepJson.parseObject("\"[[Z\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[B\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[S\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[C\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[I\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[J\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[F\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[D\"", Class.class));
		System.out.println("decode: " + DeepJson.parseObject("\"[[Ljava.lang.Object;\"", Class.class));

		try {
			System.out.println("decode: " + DeepJson.parseObject("\"[V\"", Class.class));
		} catch (Exception e) {
			System.out.println("\"[V\" decode: failed");
		}

		try {
			System.out.println("decode: " + DeepJson.parseObject("\"[[Ljava.lang.Object111;\"", Class.class));
		} catch (Exception e) {
			System.out.println("\"[[Ljava.lang.Object111;\" decode: failed");
		}

		try {
			System.out.println("decode: " + DeepJson.parseObject("\"[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[I\"", Class.class));
		} catch (Exception e) {
			System.out.println("\"[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[I\" decode: failed");
		}
	}
}