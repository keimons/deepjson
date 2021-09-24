package com.keimons.deepjson.test.codec.collection;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link Collection}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CollectionTest {

	@Test
	@SuppressWarnings("unchecked")
	public void test() {
		System.out.println("----------------> Collection Test <----------------");
		List<String> list = new ArrayList<>();
		list.add("test0");
		list.add("test1");
		list.add("test2");
		list.add("test3");
		String json = DeepJson.toJsonString(list);
		System.out.println("encode: " + json);
		List<String> result = DeepJson.parseObject(json, List.class);
		System.out.println("decode: " + DeepJson.toJsonString(result));
	}
}