package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * {@link Object[]}对象数组编解码测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ObjectArrayCodecTest {

	@Test
	public void testDecode() throws ClassNotFoundException {
		String json0 = "[true, false, \"true\", \"false\"]";
		Boolean[] booleans = DeepJson.parseObject(json0, Boolean[].class);
		System.out.println(Arrays.toString(booleans));
		// TODO 泛型类型测试
		// TODO 抽象类测试
	}
}