package com.keimons.deepjson.test.performance.primitive;

import com.alibaba.fastjson.JSONObject;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.performance.BasePerformanceTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * float性能测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class FloatTest extends BasePerformanceTest {

	@Test
	public void fastValueTest() {
		System.out.println("---------------- value ----------------");
		Random random = new Random(speed);
		List<Float> floats = new ArrayList<>(times);
		for (int i = 0; i < times; i++) {
			floats.add(random.nextFloat());
		}
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				JSONObject.toJSONString(floats.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void deepValueTest() {
		System.out.println("---------------- value ----------------");
		Random random = new Random(speed);
		List<Float> floats = new ArrayList<>(times);
		for (int i = 0; i < times; i++) {
			floats.add(random.nextFloat());
		}
		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				DeepJson.toJsonString(floats.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void fastFieldTest() {
		System.out.println("---------------- field ----------------");

		List<FloatNode> nodes = createFieldList();

		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				JSONObject.toJSONString(nodes.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	@Test
	public void deepFieldTest() {
		System.out.println("---------------- field ----------------");

		List<FloatNode> nodes = createFieldList();

		startLoopTest();
		for (int i = 0; i < loop; i++) {
			startTimesTest();
			for (int j = 0; j < times; j++) {
				DeepJson.toJsonString(nodes.get(j));
			}
			finishTimesTest(i);
		}
		finishLoopTest();
	}

	private List<FloatNode> createFieldList() {
		Random random = new Random(speed);
		List<FloatNode> nodes = new ArrayList<>(times);
		for (int i = 0; i < times; i++) {
			FloatNode node = new FloatNode();
			node.setValue0(random.nextFloat());
			node.setValue1(random.nextFloat());
			node.setValue2(random.nextFloat());
			node.setValue3(random.nextFloat());
			node.setValue4(random.nextFloat());
			node.setValue5(random.nextFloat());
			node.setValue6(random.nextFloat());
			node.setValue7(random.nextFloat());
			node.setValue8(random.nextFloat());
			node.setValue9(random.nextFloat());
			nodes.add(node);
		}
		return nodes;
	}
}