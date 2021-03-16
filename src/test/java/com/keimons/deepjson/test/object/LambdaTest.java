package com.keimons.deepjson.test.object;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.NormalNode;
import org.junit.jupiter.api.Test;

/**
 * Lambda 序列化测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class LambdaTest {

	@Test
	public void test() {
		NormalNode node = new NormalNode();
		Runnable runnable = () -> System.out.println(node.getNode1());
		System.out.println(DeepJson.toJsonString(runnable));
	}
}