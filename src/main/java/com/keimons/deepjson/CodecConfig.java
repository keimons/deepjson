package com.keimons.deepjson;

import com.keimons.deepjson.util.ArrayUtil;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.ReflectUtil;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.*;

/**
 * DeepJson配置文件
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CodecConfig {

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

	/**
	 * 默认实现
	 */
	public static final Map<Node, Type> MAPPER = new ConcurrentHashMap<Node, Type>();

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

		// default
//		DEFAULT.put(Map.class, HashMap.class);
//		DEFAULT.put(AbstractMap.class, HashMap.class);
//		DEFAULT.put(SortedMap.class, TreeMap.class);
//		DEFAULT.put(NavigableMap.class, TreeMap.class);
//		DEFAULT.put(ConcurrentMap.class, ConcurrentHashMap.class);
//		DEFAULT.put(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class);
//
//		DEFAULT.put(Collection.class, ArrayList.class);
//		DEFAULT.put(List.class, ArrayList.class);
//		DEFAULT.put(Queue.class, LinkedList.class);
//		DEFAULT.put(Deque.class, LinkedList.class);
//		DEFAULT.put(BlockingQueue.class, LinkedBlockingQueue.class);
//		DEFAULT.put(BlockingDeque.class, LinkedBlockingDeque.class);
//		DEFAULT.put(Set.class, HashSet.class);
//		DEFAULT.put(SortedSet.class, TreeSet.class);
//		DEFAULT.put(NavigableSet.class, TreeSet.class);
	}

	public static void addWrite(Class<?> clazz) {
		WHITE_OBJECT.add(clazz);
	}

	public static void removeWrite(Class<?> clazz) {
		WHITE_OBJECT.remove(clazz);
	}

	public static void clearWrite() {
		WHITE_OBJECT.clear();
	}

	public static boolean containsClass(Class<?> clazz) {
		return WHITE_OBJECT.contains(clazz);
	}

	public static void registerMapper(Type[] types, Type type) {
		MAPPER.put(new Node(types), type);
	}

	public static Type getType(Type[] types) {
		return MAPPER.get(new Node(types));
	}

	static {
		{
			Type kt = ReflectUtil.makeTypeVariable(Map.class, "K", new Type[]{Object.class});
			Type vt = ReflectUtil.makeTypeVariable(Map.class, "V", new Type[]{Object.class});
			ParameterizedType pt = ReflectUtil.makeParameterizedType(null, Map.class, kt, vt);
			registerMapper(new Type[]{pt, Serializable.class}, HashMap.class); // same as HashMap<Object, Object>
		}
		{
			Type kt = ReflectUtil.makeWildcardType(new Type[]{Object.class}, null);
			Type vt = ReflectUtil.makeWildcardType(new Type[]{Object.class}, null);
			ParameterizedType pt = ReflectUtil.makeParameterizedType(null, Map.class, kt, vt);
			registerMapper(new Type[]{pt, Serializable.class}, HashMap.class); // same as HashMap<?, ?>
		}
	}

	/**
	 * java
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 **/
	private static class Node {

		private final Type[] types;

		private final int hashcode;

		private final int length;

		public Node(Type[] types) {
			this.types = types;
			this.length = types.length;
			this.hashcode = ArrayUtil.unifiedHashcode(types);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Node node = (Node) o;
			if (node.length != length) {
				return false;
			}
			Type[] types = node.types;
			start:
			for (Type type : this.types) {
				for (Type target : types) {
					if (type != null && type.equals(target)) {
						continue start;
					}
				}
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return hashcode;
		}
	}
}