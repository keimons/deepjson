package com.keimons.deepjson.test.support;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * Google guava {@link Multimap} test.
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class GuavaMultimapTest {

	@Test
	public void test() {
		Multimap<String, Integer> multimap = HashMultimap.create();
		multimap.put("t1", 1);
		multimap.put("t1", 2);
		multimap.put("t1", 1);
		multimap.put("t2", 3);
		multimap.put("t2", 3);
		multimap.put("t2", 1);
		System.out.println(new Gson().toJson(multimap));
		System.out.println(DeepJson.toJsonString(multimap));
	}
}