package com.keimons.deepjson.test.codec.adder;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * {@link LongAdder}和{@link DoubleAdder}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class AdderTest {

	@Test
	public void test() {
		System.out.println("----------------> LongAdder Test <----------------");
		LongAdder adder1 = new LongAdder();
		adder1.add(100L);
		System.out.println("encode: " + DeepJson.toJsonString(adder1)); // 100
		adder1 = DeepJson.parseObject("100", LongAdder.class);
		System.out.println("decode: " + adder1.sum());

		System.out.println();

		System.out.println("----------------> DoubleAdder Test <----------------");
		DoubleAdder adder2 = new DoubleAdder();
		adder2.add(100d);
		System.out.println("encode: " + DeepJson.toJsonString(adder2)); // 100.0
		adder2 = DeepJson.parseObject("100.0", DoubleAdder.class);
		System.out.println("decode: " + adder2.sum());
	}
}