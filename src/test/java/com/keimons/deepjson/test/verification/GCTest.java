package com.keimons.deepjson.test.verification;

import com.sun.management.GcInfo;
import org.junit.jupiter.api.Test;

import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GC监控
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.7
 **/
public class GCTest {

	@Test
	public void test() {
		NotificationListener listener = new NotificationListener() {
			@Override
			public void handleNotification(Notification notification, Object name) {
				CompositeData cd = (CompositeData) notification.getUserData();
				cd = (CompositeData) cd.get("gcInfo");
				GcInfo gcInfo = GcInfo.from(cd);
				StringBuilder builder = new StringBuilder();
				builder.append(gcInfo.getCompositeType().getTypeName())
						.append(":")
						.append(gcInfo.getId())
						.append("\n")
				;
				builder.append("------------before------------\n");
				for (Map.Entry<String, MemoryUsage> entry : gcInfo.getMemoryUsageBeforeGc().entrySet()) {
					builder.append(entry.getKey())
							.append(":")
							.append(entry.getValue().toString())
							.append("\n")
					;
				}
				builder.append("------------after------------\n");
				for (Map.Entry<String, MemoryUsage> entry : gcInfo.getMemoryUsageAfterGc().entrySet()) {
					builder.append(entry.getKey())
							.append(":")
							.append(entry.getValue().toString())
							.append("\n")
					;
				}
				System.out.println(builder.toString());
			}
		};

		List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();

		for (GarbageCollectorMXBean bean : beans) {
			((NotificationBroadcaster) bean).addNotificationListener(listener, null, bean.getName());
			System.out.println("name: " + bean.getName());
		}

		// Force garbage collection

		// -ea -Xms1024m -Xmx1024m -verbose:gc
		List<SoftReference<byte[]>> soft = new ArrayList<SoftReference<byte[]>>(32 * 1024);
		List<WeakReference<byte[]>> weak = new ArrayList<WeakReference<byte[]>>(32 * 1024);
		for (int i = 0; i < 64 * 1024; i++) {
			if ((i & 1) == 1) {
				soft.add(new SoftReference<byte[]>(new byte[128 * 1024]));
			} else {
				weak.add(new WeakReference<byte[]>(new byte[128 * 1024]));
			}
		}
		Reference.reachabilityFence(soft);
		Reference.reachabilityFence(weak);
	}
}