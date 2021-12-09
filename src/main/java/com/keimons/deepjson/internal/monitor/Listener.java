package com.keimons.deepjson.internal.monitor;

import com.sun.management.GcInfo;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 内存监视器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Listener {

	// G1垃圾回收
	private static final String G1_NAME_FULL = "G1 Old Generation";

	// GC监听器
	private static final Map<String, NotificationListener> LISTENERS = new HashMap<String, NotificationListener>();

	private static final ListenerType TYPE = ListenerType.G1;

	public void register(String name, final GCConsumer consumer) {
		NotificationListener listener = TYPE.createListener(consumer);
		List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean bean : beans) {
			((NotificationBroadcaster) bean).addNotificationListener(listener, null, bean.getName());
		}
		LISTENERS.put(name, listener);
	}

	public void remove(String name) {
		NotificationListener listener = LISTENERS.remove(name);
		if (listener != null) {
			List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();
			for (GarbageCollectorMXBean bean : beans) {
				try {
					((NotificationBroadcaster) bean).removeNotificationListener(listener);
				} catch (ListenerNotFoundException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * 保留最近10次{@code Full GC}时间
	 */
	private static final LinkedList<Long> fgcTime = new LinkedList<Long>();

	/**
	 * 记录时间
	 */
	private static void recordTime() {
		long time = System.currentTimeMillis();
		fgcTime.offer(time);
		while (fgcTime.size() > 10) {
			fgcTime.pollFirst();
		}
	}

	interface Listen {

		NotificationListener createListener(final GCConsumer consumer);
	}

	private enum ListenerType implements Listen {
		NONE {
			@Override
			public NotificationListener createListener(final GCConsumer consumer) {
				return null;
			}
		},
		G1 {
			@Override
			public NotificationListener createListener(final GCConsumer consumer) {
				return new NotificationListener() {

					@Override
					public void handleNotification(Notification notification, Object name) {
						recordTime();

						CompositeData cd = (CompositeData) notification.getUserData();
						Object gcName = cd.get("gcName");
						if (!gcName.equals(G1_NAME_FULL)) {
							return;
						}
						// @see com.sun.management.internal.GarbageCollectionNotifInfoCompositeData
						GcInfo info = GcInfo.from((CompositeData) cd.get("gcInfo"));
						Map<String, MemoryUsage> usage = info.getMemoryUsageAfterGc();
						MemoryUsage eden = usage.get("G1 Eden Space");
						MemoryUsage survivor = usage.get("G1 Survivor Space");
						MemoryUsage old = usage.get("G1 Old Gen");

						long max = old.getMax();
						long used = eden.getUsed() + survivor.getUsed() + old.getUsed();
						consumer.accept(Math.round(used * 10000f / max) * 0.01f, fgcTime);
					}
				};
			}
		};
	}
}