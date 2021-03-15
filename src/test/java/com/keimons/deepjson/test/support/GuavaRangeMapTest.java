package com.keimons.deepjson.test.support;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * Google Guava
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class GuavaRangeMapTest {

	@Test
	public void test() {
		RangeMap<Integer, String> map = TreeRangeMap.create();
		map.put(Range.open(1, 10), "value1");
		map.put(Range.open(12, 23), "value2");
		map.put(Range.open(24, 46), "value3");
		for (Map.Entry<Range<Integer>, String> entry : map.asMapOfRanges().entrySet()) {

		}
		System.out.println(map.get(5));
		System.out.println(DeepJson.toJsonString(map));
	}
}