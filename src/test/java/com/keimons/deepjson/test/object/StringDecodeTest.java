package com.keimons.deepjson.test.object;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@link String}解析测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringDecodeTest {

	@Test
	public void test() {
		String json = "\"1\u0042\r\n34\"//";
		System.out.println(DeepJson.parseObject(json, String.class));
	}
}