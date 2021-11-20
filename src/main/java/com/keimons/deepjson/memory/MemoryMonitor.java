package com.keimons.deepjson.memory;

import com.keimons.deepjson.util.PlatformUtil;

import java.lang.ref.Cleaner;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 内存监视器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MemoryMonitor {

	/**
	 * 清理对象，确保只有一个
	 */
	private static final Cleaner CLEANER = Cleaner.create();

	/**
	 * 保留最近10次Full GC时间
	 */
	private static final Map<String, Monitor> MONITORS = new HashMap<String, Monitor>();

	/**
	 * 保留最近10次Full GC时间
	 */
	private static final LinkedList<Long> FULL_GC_TIME = new LinkedList<Long>();

	/**
	 * 初始化监视器
	 *
	 * @param threshold 触发监控的阈值
	 * @param action    监控触发后的行为
	 */
	public static void register(String name, final int threshold, final MonitorConsumer action) {
		Monitor monitor = new Monitor(threshold, action);
		monitor.monitor();
		MONITORS.put(name, monitor);
	}

	public static void remove(String name) {
		Monitor monitor = MONITORS.get(name);
		if (monitor != null) {
			monitor.work = false;
		}
	}

	private static void recordTime() {
		long time = System.currentTimeMillis();
		FULL_GC_TIME.offer(time);
		while (FULL_GC_TIME.size() > 10) {
			FULL_GC_TIME.pollFirst();
		}
	}

	private static class Monitor implements Runnable {

		private final int threshold;

		private final MonitorConsumer action;

		/**
		 * 内存监控
		 * <p>
		 * 设计的目的在于，当发生{@code Full GC}时，DeepJson能够接收到通知，并且对缓存策略进行调整。
		 * <p>
		 * 注意：在android和java环境中会有不同的表现。当android环境中，优先回收软引用，在java环境中，优先扩展堆。
		 */
		private SoftReference<Object> WATCHER;

		boolean work = true;

		private Monitor(int threshold, MonitorConsumer action) {
			this.threshold = threshold;
			this.action = action;
		}

		@Override
		public void run() {
			assert WATCHER.get() == null;
			recordTime();
			float usage = PlatformUtil.memoryUsage();
			if (usage > threshold) {
				action.accept(PlatformUtil.memoryUsage(), FULL_GC_TIME);
			}
			if (work) {
				monitor();
			}
		}

		public void monitor() {
			Object observed = new Object();
			WATCHER = new SoftReference<Object>(observed);
			CLEANER.register(observed, this);
		}
	}
}