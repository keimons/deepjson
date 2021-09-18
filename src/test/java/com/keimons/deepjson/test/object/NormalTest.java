package com.keimons.deepjson.test.object;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * 普通对象测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class NormalTest {

	@Test
	public void test() {
		NormalNode node = new NormalNode();
		System.out.println(DeepJson.toJsonString(node));
	}
}