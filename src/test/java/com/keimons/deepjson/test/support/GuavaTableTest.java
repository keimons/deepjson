package com.keimons.deepjson.test.support;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * Google guava {@link Table} test.
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class GuavaTableTest {

	@Test
	public void test() {
		Table<Integer, Integer, Integer> table = HashBasedTable.create();
		table.put(1, 1, 100);
		System.out.println(JSONObject.toJSONString(table));
		System.out.println(DeepJson.toJsonString(table));
		System.out.println(new Gson().toJson(table));
	}
}