package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.Json;
import org.junit.jupiter.api.Test;

/**
 * {@link Json}编解码测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonTest {

	@Test
	public void test() {
		Json json = new Json();
		json.put("test1", 1024);
		json.put("test2", 1024L);
		json.put("test3", "test1");
		String str1 = DeepJson.toJsonString(json);
		System.out.println(str1);
		Json r1 = DeepJson.parseObject(str1);
		System.out.println(DeepJson.toJsonString(r1));
		json.clear();
		json.add("test1");
		json.add("test2");
		json.add("test3");
		json.add(1024);
		json.add(1024L);
		String str2 = DeepJson.toJsonString(json);
		System.out.println(str2);
		Json r2 = DeepJson.parseObject(str2);
		System.out.println(DeepJson.toJsonString(r2));
	}
}