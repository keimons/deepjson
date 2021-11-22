package com.keimons.deepjson.test.monitor;

import com.keimons.deepjson.monitor.Monitor;
import com.keimons.deepjson.monitor.MonitorConsumer;
import com.keimons.deepjson.util.PlatformUtil;
import org.junit.jupiter.api.Test;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link Monitor}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MonitorTest {

	@Test
	public void test() throws InterruptedException {
		Monitor.register("MonitorTest", new MonitorConsumer() {

			int times = 0;

			@Override
			public void accept(float usage, List<Long> fgcTime) {
				times++;
				System.out.println("第 " + times + " 次，当前内存占用：" + usage);
				System.out.println("第 " + times + " 次，历史执行时间：" + Arrays.toString(fgcTime.toArray()));
				System.out.println(PlatformUtil.memoryUsage());
			}
		});

		List<SoftReference<byte[]>> list = new ArrayList<SoftReference<byte[]>>();

		for (int i = 0; i < 16 * 1024; i++) {
			list.add(new SoftReference<byte[]>(new byte[1024 * 1024]));
		}

		Thread.sleep(3000);
	}
}