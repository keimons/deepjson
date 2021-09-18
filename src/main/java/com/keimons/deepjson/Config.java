package com.keimons.deepjson;

import com.keimons.deepjson.util.PlatformUtil;

import java.util.*;
import java.util.concurrent.*;

/**
 * DeepJson配置文件
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Config {

	public static final int HIGHEST = 18;

	/**
	 * run as debug.
	 */
	public static final boolean DEBUG;
	/**
	 * 集合白名单（保守策略）
	 * <p>
	 * 只有处于白名单中的类，才能在编解码时，生成自描述信息。
	 */
	@SuppressWarnings("rawtypes")
	public static final Set<Class<? extends Collection>> WHITE_COLLECTION = new HashSet<Class<? extends Collection>>();
	/**
	 * 映射白名单（保守策略）
	 * <p>
	 * 只有处于白名单中的类，才能在编解码时，生成自描述信息。
	 */
	@SuppressWarnings("rawtypes")
	public static final Set<Class<? extends Map>> WHITE_MAP = new HashSet<Class<? extends Map>>();
	/**
	 * 对象白名单（保守策略）
	 * <p>
	 * 只有处于白名单中的类，才能在编解码时，生成自描述信息。
	 */
	public static final Set<Class<?>> WHITE_OBJECT = new HashSet<Class<?>>();

	static {
		// 预热编译器
		String property = System.getProperty("com.keimons.deepjson.Debug");
		boolean debug = false;
		if (property != null) {
			debug = Boolean.parseBoolean(property);
		}
		DEBUG = debug;
	}

	static {
		// region default white list
		// java.util
		WHITE_COLLECTION.add(ArrayList.class);
		WHITE_COLLECTION.add(LinkedList.class);
		WHITE_COLLECTION.add(Vector.class);
		WHITE_COLLECTION.add(HashSet.class);
		// comparator is not yet supported.
		// COLLECTION_WHITE.add(TreeSet.class);
		WHITE_COLLECTION.add(LinkedHashSet.class);
		WHITE_COLLECTION.add(ArrayDeque.class);

		// java.util.concurrent
		WHITE_COLLECTION.add(ArrayBlockingQueue.class);
		WHITE_COLLECTION.add(ConcurrentLinkedQueue.class);
		// comparator is not yet supported.
		// COLLECTION_WHITE.add(ConcurrentSkipListSet.class);
		WHITE_COLLECTION.add(CopyOnWriteArrayList.class);
		WHITE_COLLECTION.add(CopyOnWriteArraySet.class);
		// no idea of java.util.concurrent.DelayQueue
		// COLLECTION_WHITE.add(DelayQueue.class);
		WHITE_COLLECTION.add(LinkedBlockingDeque.class);
		WHITE_COLLECTION.add(LinkedBlockingQueue.class);
		// comparator is not yet supported. lambda.
		// COLLECTION_WHITE.add(PriorityBlockingQueue.class);
		if (PlatformUtil.javaVersion() >= 7) {
			WHITE_COLLECTION.add(ConcurrentLinkedDeque.class);
			WHITE_COLLECTION.add(LinkedTransferQueue.class);
		}
		// endregion

		// region map

		// java.util
		WHITE_MAP.add(EnumMap.class);
		WHITE_MAP.add(HashMap.class);
		WHITE_MAP.add(IdentityHashMap.class);
		WHITE_MAP.add(LinkedHashMap.class);
		// comparator is not yet supported. lambda.
		// MAP_WHITE.add(TreeMap.class);
		// no idea of java.util.WeakHashMap
		// MAP_WHITE.add(WeakHashMap.class);
		// java.util.concurrent
		WHITE_MAP.add(ConcurrentHashMap.class);
		// comparator is not yet supported. lambda.
		// MAP_WHITE.add(ConcurrentSkipListMap.class);
		// endregion
	}

	public static void addWrite(Class<?> clazz) {
		WHITE_OBJECT.add(clazz);
	}

	public static void removeWrite(Class<?> clazz) {
		WHITE_OBJECT.remove(clazz);
	}

	public static boolean containsClass(Class<?> clazz) {
		return WHITE_OBJECT.contains(clazz);
	}
}