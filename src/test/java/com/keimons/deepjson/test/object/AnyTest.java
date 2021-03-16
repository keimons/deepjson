package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.NormalNode;
import com.keimons.deepjson.util.UnsafeUtil;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.LongAdder;

/**
 * 随机测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class AnyTest {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	@Test
	public void test() throws Exception {
		LongAdder adder = new LongAdder();
		NormalNode node = new NormalNode();
		Runnable runnable = () -> {
			System.out.println(adder.sum());
			System.out.println(node.getNode1());
		};
		adder.increment();
		adder.increment();
		System.out.println(runnable.getClass()); // AnyTest$$Lambda$286/0x000000080014d840
		Class<?> clazz = runnable.getClass();
		System.out.println(clazz.getName());
		Field arg$1 = clazz.getDeclaredField("arg$1");
		Runnable r = (Runnable) unsafe.allocateInstance(clazz);
		unsafe.putObject(r, unsafe.objectFieldOffset(arg$1), node);
		r.run();
		Runnable r1 = () -> System.out.println(node.getNode2());
		System.out.println(r1.getClass());
		System.out.println(DeepJson.toJsonString(runnable));
		System.out.println(JSONObject.toJSONString(runnable));
		System.out.println(new Gson().toJson(adder));
	}
}