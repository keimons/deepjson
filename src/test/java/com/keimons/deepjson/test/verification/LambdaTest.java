package com.keimons.deepjson.test.verification;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * {@code lambda}序列化和反序列化验证
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class LambdaTest {

	int value = 1024;

	// -verbose:gc
	@Test
	public void test() {
		int number = 1024;
		Runnable runnable = () -> System.out.println(this.value + number);
		runnable.run();
		String json = DeepJson.toJsonString(runnable);
		System.out.println("{\"arg$1\":{\"value\":1024,\"name\":{\"key\":\"lambda test\"}},\"arg$2\":1024}");
		runnable = DeepJson.parseObject("{\"arg$1\":{\"value\":1024,\"name\":{\"key\":\"lambda test\"}},\"arg$2\":1024}", runnable.getClass());
		runnable.run();
	}
}