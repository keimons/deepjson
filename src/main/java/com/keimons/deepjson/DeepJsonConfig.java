package com.keimons.deepjson;

import java.util.*;
import java.util.concurrent.*;

/**
 * DeepJson配置文件
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class DeepJsonConfig {

	/**
	 * 集合白名单（保守策略）
	 * <p>
	 * 只有处于白名单中的类，才能在序列化和反序列化时，生成自描述信息。
	 */
	@SuppressWarnings("rawtypes")
	public static final Set<Class<? extends Collection>> WHITE_COLLECTION = new HashSet<>();

	/**
	 * 映射白名单（保守策略）
	 * <p>
	 * 只有处于白名单中的类，才能在序列化和反序列化时，生成自描述信息。
	 */
	@SuppressWarnings("rawtypes")
	public static final Set<Class<? extends Map>> WHITE_MAP = new HashSet<>();

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
		WHITE_COLLECTION.add(ConcurrentLinkedDeque.class);
		WHITE_COLLECTION.add(ConcurrentLinkedQueue.class);
		// comparator is not yet supported.
		// COLLECTION_WHITE.add(ConcurrentSkipListSet.class);
		WHITE_COLLECTION.add(CopyOnWriteArrayList.class);
		WHITE_COLLECTION.add(CopyOnWriteArraySet.class);
		// no idea of java.util.concurrent.DelayQueue
		// COLLECTION_WHITE.add(DelayQueue.class);
		WHITE_COLLECTION.add(LinkedBlockingDeque.class);
		WHITE_COLLECTION.add(LinkedBlockingQueue.class);
		WHITE_COLLECTION.add(LinkedTransferQueue.class);
		// comparator is not yet supported. lambda.
		// COLLECTION_WHITE.add(PriorityBlockingQueue.class);
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
}