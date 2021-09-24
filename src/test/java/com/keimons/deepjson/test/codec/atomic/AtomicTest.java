package com.keimons.deepjson.test.codec.atomic;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Atomic测试
 * <p>
 * {@link AtomicBoolean}测试
 * {@link AtomicInteger}测试
 * {@link AtomicLong}测试
 * {@link AtomicReference}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AtomicTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test() {
		System.out.println("----------------> Atomic Test <----------------");
		AtomicBoolean az = new AtomicBoolean(true);
		String json = DeepJson.toJsonString(az);
		System.out.println("encode: " + json);
		AtomicBoolean abr = DeepJson.parseObject(json, AtomicBoolean.class);
		System.out.println("decode: " + DeepJson.toJsonString(abr));

		AtomicInteger ai = new AtomicInteger(20000);
		json = DeepJson.toJsonString(ai);
		System.out.println("encode: " + json);
		AtomicInteger air = DeepJson.parseObject(json, AtomicInteger.class);
		System.out.println("decode: " + DeepJson.toJsonString(air));

		AtomicLong aj = new AtomicLong(20000000000L);
		json = DeepJson.toJsonString(aj);
		System.out.println("encode: " + json);
		AtomicLong ajr = DeepJson.parseObject(json, AtomicLong.class);
		System.out.println("decode: " + DeepJson.toJsonString(ajr));

		AtomicReference<AtomicTest> al = new AtomicReference<AtomicTest>();
		json = DeepJson.toJsonString(al);
		System.out.println("encode: " + json);
		AtomicReference<AtomicTest> alr = DeepJson.parseObject(json, AtomicReference.class);
		System.out.println("decode: " + DeepJson.toJsonString(alr));
	}
}