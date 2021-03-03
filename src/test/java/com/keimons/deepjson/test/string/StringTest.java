package com.keimons.deepjson.test.string;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * String类型测试
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.7
 **/
public class StringTest {

	@Test
	public void test() {
		System.out.println(DeepJson.toJsonString("test json"));
	}
}