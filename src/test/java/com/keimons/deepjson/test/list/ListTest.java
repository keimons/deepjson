package com.keimons.deepjson.test.list;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * List序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ListTest {

	@Test
	public void test() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			list.add(i);
		}
		System.out.println(DeepJson.toJsonString(list));
	}
}