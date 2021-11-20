package com.keimons.deepjson.test.object;

import com.keimons.deepjson.util.PlatformUtil;
import org.junit.jupiter.api.Test;

import java.lang.ref.Cleaner;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 引用测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReferenceTest {

	@Test
	public void test() throws InterruptedException {
		Object obj = new Object();
		SoftReference<Object> soft1 = new SoftReference<Object>(obj);
		SoftReference<Object> soft2 = new SoftReference<Object>(obj);
		SoftReference<Object> soft3 = new SoftReference<Object>(obj);
		SoftReference<Object> soft4 = new SoftReference<Object>(obj);
		SoftReference<Object> soft5 = new SoftReference<Object>(obj);

		Cleaner cleaner = Cleaner.create();
		cleaner.register(obj, () -> {
			System.out.println(PlatformUtil.memoryUsage());
			System.out.println(Thread.currentThread().getName());
		});
		obj = null;
		List<SoftReference<byte[]>> list = new ArrayList<SoftReference<byte[]>>();
		int sum = 0;
		for (int i = 0; i < 10000; i++) {
			byte[] bytes = new byte[256 * 1024];
			bytes[0] = 2;
			sum += bytes[0] + bytes[1] + bytes.length;
			list.add(new SoftReference<byte[]>(bytes));
			Thread.sleep(10);
		}
		System.out.println("begin " + sum + " " + list.size());
		System.gc();
		System.out.println("finish");
		Thread.sleep(3000);
		Reference.reachabilityFence(soft1);
		Reference.reachabilityFence(soft2);
		Reference.reachabilityFence(soft3);
		Reference.reachabilityFence(soft4);
		Reference.reachabilityFence(soft5);
	}
}