package com.keimons.deepjson.internal.monitor;

import com.keimons.deepjson.util.PlatformUtil;

import java.lang.ref.Cleaner;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 内存监视管理
 * <p>
 * 启动特定的线程，对内存进行监控。当获取内存使用情况时，并不一定是{@code Full GC}后的内存使用情况。
 * 这只是一个近似值，用于评估内存的使用情况。所有消费函数应该尽可能快的执行完。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Monitor {

	/**
	 * 清理对象
	 */
	private static final Cleaner CLEANER = Cleaner.create();

	/**
	 * 内存监视器
	 */
	private static final Map<String, MemoryMonitor> MONITORS = new HashMap<String, MemoryMonitor>();

	/**
	 * 注册一个监视器
	 *
	 * @param name     监视器名称
	 * @param consumer GC发生时触发行为
	 */
	public static void register(String name, final GCConsumer consumer) {
		MemoryMonitor monitor = new MemoryMonitor(consumer);
		monitor.monitor();
		MONITORS.put(name, monitor);
	}

	/**
	 * 移除一个监视器
	 * <p>
	 * 监视器并不会立即被移除，而是等到下次GC发生时，不再触发任何行为。
	 *
	 * @param name 监视器名称
	 */
	public static void remove(String name) {
		MemoryMonitor monitor = MONITORS.remove(name);
		if (monitor != null) {
			monitor.active = false;
		}
	}

	/**
	 * 内存监视器
	 */
	private static class MemoryMonitor implements Runnable {

		/**
		 * 消费函数
		 * <p>
		 * 当{@code Full GC}发生后，如果监视器{@link #active}仍处于活跃状态，则执行消费函数。
		 */
		private final GCConsumer consumer;

		/**
		 * 监视器是否处于活动中
		 * <p>
		 * 当移除监视器时，监视器并不会真的被移除，而是等到{@code Full GC}发生时，不采取任何行为。
		 */
		volatile boolean active = true;

		/**
		 * 内存监控
		 * <p>
		 * 设计的目的在于，当发生{@code Full GC}时，DeepJson能够接收到通知，并且对缓存策略进行调整。
		 * <p>
		 * 注意：在android和java环境中会有不同的表现。当android环境中，优先回收软引用，在java环境中，优先扩展堆。
		 */
		SoftReference<Object> watcher;

		/**
		 * 保留最近10次{@code Full GC}时间
		 */
		private final LinkedList<Long> fgcTime = new LinkedList<Long>();

		/**
		 * 构造一个内存监视器
		 *
		 * @param consumer {@code Full GC}发生时触发
		 */
		private MemoryMonitor(GCConsumer consumer) {
			this.consumer = consumer;
		}

		@Override
		public void run() {
			if (active) {
				recordTime();
				consumer.accept(PlatformUtil.memoryUsage(), fgcTime);
				monitor();
			}
		}

		/**
		 * 开始监视
		 */
		public void monitor() {
			Object observed = new Object();
			watcher = new SoftReference<Object>(observed);
			CLEANER.register(observed, this);
		}

		/**
		 * 记录时间
		 */
		private void recordTime() {
			long time = System.currentTimeMillis();
			fgcTime.offer(time);
			while (fgcTime.size() > 10) {
				fgcTime.pollFirst();
			}
		}
	}
}