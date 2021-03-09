package com.keimons.deepjson.test.util;

import com.keimons.deepjson.util.RyuDouble;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * 序列化工具测试类
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class SerializerUtilTest {

	@Test
	public void test() {
		Random random = new Random();

		double d = random.nextDouble();
		System.out.println(Double.toString(d).length());
		System.out.println(RyuDouble.length(d));

//		for (; ; ) {
		for (int i = 0; i < 1000000; i++) {
			double value = random.nextDouble();
			if (Double.toString(value).length() != RyuDouble.length(value)) {
				System.out.println(value);
				System.out.println(Double.toString(value).length());
				System.out.println(RyuDouble.length(value));
			}
		}
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		long startTime = System.nanoTime();
		for (int i = 0; i < 10000000; i++) {
			double value = random.nextDouble();
			RyuDouble.length(value);
		}
		System.out.println("length: " + (System.nanoTime() - startTime) / 100000f);

		startTime = System.nanoTime();
		for (int i = 0; i < 10000000; i++) {
			double value = random.nextDouble();
			Double.toString(value).length();
		}
		System.out.println("string: " + (System.nanoTime() - startTime) / 100000f);
	}
}