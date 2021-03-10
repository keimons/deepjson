package com.keimons.deepjson.test.performance.list;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.performance.BasePerformanceTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * List性能测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class ListTest extends BasePerformanceTest {

	private static final char[] values = new char[512];

	static {
		for (int i = 0; i < 512; i++) {
			values[i] = (char) i;
		}
	}

	@Test
	public void fastStringTest() {
		List<String> items = createStringList();
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				JSONObject.toJSONString(items.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void deepStringTest() {
		List<String> items = createStringList();
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				DeepJson.toJsonString(items.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void fastIntegerTest() {
		List<Integer> items = createIntegerList();
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				JSONObject.toJSONString(items.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void deepIntegerTest() {
		List<Integer> items = createIntegerList();
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				DeepJson.toJsonString(items.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	/**
	 * 测试用例
	 *
	 * @return 测试数据
	 */
	private List<String> createStringList() {
		List<String> list = new ArrayList<>(times);
		Random random = new Random(speed);
		for (int i = 0; i < times; i++) {
			int count = 5 + random.nextInt(20);
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < count; j++) {
				sb.append(values[j]);
			}
			list.add(sb.toString());
		}
		return list;
	}

	/**
	 * 测试用例
	 *
	 * @return 测试数据
	 */
	private List<Integer> createIntegerList() {
		List<Integer> list = new ArrayList<>(times);
		Random random = new Random(speed);
		for (int i = 0; i < times; i++) {
			list.add(random.nextInt());
		}
		return list;
	}
}