package com.keimons.deepjson.util;

import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 使用{@code ==}比较键的映射，但在映射中存在对象作为键并不会阻止它被垃圾收集（如 WeakHashMap）。
 * 这个类没有实现{@link Map}接口，因为很难确保{@link Map#entrySet()}上的迭代器的语义正确。
 * <p>
 * 当且仅当对象存在时，才可以使用{@link #get(Object)}方法。当使用{@link #clear()}方法时，会使
 * 所有映射失效，但是并不是清空所有映射。
 * <p>
 * 不提供{@code remove(Object)}方法，因为这个集合更趋近于{@link Set}。
 * <p>
 * 此映射支持{@code null}作为键。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class WeakIdentityHashMap<K> extends AbstractMap<K> {

	private static final Object NULL = new Object();

	/**
	 * 无参数构造函数使用的初始容量。
	 */
	private static final int DEFAULT_CAPACITY = 32;

	/**
	 * 最大容量
	 * <p>
	 * 该值必须是2的幂，并且{@code 8 <= MAXIMUM_CAPACITY < (1 << 30)}。实际上，
	 * 计数器中最多可以容纳{@code #MAXIMUM_CAPACITY - 1}个元素，因为它必须至少要保
	 * 留一个空槽，否则，在{@link #put(Object, int)}时，无法判断终止查找位置，进而
	 * 死循环。
	 */
	private static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * 依赖于版本的容器
	 * <p>
	 * 这个容器无需清空，当版本升级时，容器中不论是否存在对象，都将判定为无效。
	 * 容器中的对象不需要手动清空。
	 */
	WeakNode[] table;

	/**
	 * 计算位置的hex值
	 * <p>
	 * {@code hex = table.length - 1;}
	 */
	private int hex;

	/**
	 * 当前版本
	 * <p>
	 * 如果版本升级，则版本中所有对象失效。
	 */
	private int version;

	/**
	 * 容器中元素数量
	 */
	private int size;

	/**
	 * 使用默认的预期容量构造一个新的容器。
	 *
	 * @param defaultValue 默认值
	 */
	public WeakIdentityHashMap(int defaultValue) {
		this(defaultValue, DEFAULT_CAPACITY);
	}

	/**
	 * 使用指定的预期容量构造一个新的空映射。
	 * <p>
	 * 将超过预期数量的元素放入容器中，可能会导致内部数据结构增长，这可能会有些耗时。
	 *
	 * @param defaultValue 默认值
	 * @param initCapacity 预期容量
	 * @throws IllegalArgumentException 如果{@code initCapacity}不是2的幂或小于8。
	 */
	public WeakIdentityHashMap(int defaultValue, int initCapacity) {
		super(defaultValue);
		if (initCapacity < 8) {
			throw new IllegalArgumentException("initCapacity is negative: " + initCapacity);
		}
		if (Integer.bitCount(initCapacity) != 1) {
			throw new IllegalArgumentException("initCapacity mast be 2 pow.");
		}
		init(initCapacity);
	}

	/**
	 * 返回对象的索引
	 *
	 * @param obj 要获取索引对象
	 * @param hex 模长度
	 * @return 对象在数组中的索引。
	 */
	private static int hash(Object obj, int hex) {
		int h = System.identityHashCode(obj);
		// Multiply by -127, and left-shift to use least bit as part of hash
		return ((h << 1) - (h << 8)) & hex;
	}

	/**
	 * 将初始化为具有指定初始容量的容器
	 *
	 * @param initCapacity 初始容量
	 */
	private void init(int initCapacity) {
		table = new WeakNode[initCapacity];
		hex = initCapacity - 1;
	}

	/**
	 * 存入值
	 *
	 * @param ref   节点
	 * @param index 位置
	 * @param key   映射中的键
	 * @param value 映射中的值
	 */
	private void putVal(WeakNode ref, int index, Object key, int value) {
		if (ref == null) {
			table[index] = new WeakNode(version, key, value);
		} else {
			ref.set(version, key, value);
		}
		this.size++;
	}

	/**
	 * 调整容量
	 * <p>
	 * 如果当前容量小于最大容量，则进行容量调整，如果已经达到最大容量，则不再继续调整容量。
	 *
	 * @param newCapacity 新容量，必须是2的幂
	 * @return 容量是否调整
	 * @throws IllegalStateException 容器已满
	 */
	private boolean resize(int newCapacity) {
		int newLength = newCapacity << 1;

		WeakNode[] tab = table;
		int oldLength = tab.length;
		if (oldLength == MAXIMUM_CAPACITY) {
			if (size >= MAXIMUM_CAPACITY - 1) {
				throw new IllegalStateException("Capacity exhausted.");
			}
			return false;
		}
		// 有可能会造成对象的浪费
		WeakNode[] newTable = new WeakNode[newLength];
		int newHex = newTable.length - 1;
		for (WeakNode ref : tab) {
			if (ref != null && ref.isNotEmpty(version)) {
				int index = hash(ref.getKey(), newHex);
				while (newTable[index] != null) {
					index = (++index) & newHex;
				}
				newTable[index] = ref;
			}
		}
		table = newTable;
		hex = newLength - 1;
		return true;
	}

	@Override
	public int capacity() {
		return table.length;
	}

	@Override
	public int put(@Nullable K k, int value) {
		Object key = k == null ? NULL : k;
		for (; ; ) {
			final WeakNode[] tab = table;
			final int len = tab.length;
			int index = hash(key, hex);
			WeakNode ref;
			// 判断是否为空不仅仅要判槽位是否为空，还要检测版本是否当前版本
			for (; (ref = tab[index]) != null && ref.isNotEmpty(version); index = (++index) & hex) {
				if (ref.getKey() == key) {
					int oldValue = ref.getValue();
					ref.set(version, key, value);
					return oldValue;
				}
			}

			final int size = this.size + 1;
			// 如果新增之后的1.5倍容量 大于 当前最大容量，则进行扩容。
			// 新的容量是当前容量的两倍
			if (size + (size >> 1) > len && resize(len)) {
				continue;
			}
			putVal(ref, index, key, value);
			return defaultValue;
		}
	}

	@Override
	public int putIfAbsent(@Nullable K k, int newValue) {
		Object key = k == null ? NULL : k;
		for (; ; ) {
			final WeakNode[] tab = table;
			final int len = tab.length;
			int index = hash(key, hex);
			WeakNode ref;
			// 判断是否为空不仅仅要判槽位是否为空，还要检测版本是否当前版本
			for (; (ref = tab[index]) != null && ref.isNotEmpty(version); index = (++index) & hex) {
				if (ref.getKey() == key) {
					return ref.getValue();
				}
			}

			final int size = this.size + 1;
			// 如果新增之后的1.5倍容量 大于 当前最大容量，则进行扩容。
			// 新的容量是当前容量的两倍
			if (size + (size >> 1) > len && resize(len)) {
				continue;
			}
			if (ref == null) {
				table[index] = new WeakNode(version, key, newValue);
			} else {
				ref.set(version, key, newValue);
			}
			this.size = size;
			return defaultValue;
		}
	}

	@Override
	public int get(@Nullable K key) {
		return getOrDefault(key, defaultValue);
	}

	@Override
	public int getOrDefault(@Nullable K k, int defaultValue) {
		Object key = k == null ? NULL : k;
		int index = hash(key, hex);
		while (true) {
			WeakNode ref = table[index];
			if (ref == null || ref.isEmpty(version)) {
				return defaultValue;
			}
			if (ref.getKey() == key) {
				return ref.getValue();
			}
			index = (++index) & hex;
		}
	}

	@Override
	public void clear() {
		this.size = 0;
		this.version++;
		// 每268435455次，进行一次真实的清空操作，以防止内部容器中的version溢出。
		if ((version & 0xFFFFFFF) == 0) {
			Arrays.fill(table, null);
		}
	}

	/**
	 * 带有版本的弱引用节点
	 */
	public static class WeakNode extends WeakReference<Object> implements INode<Object> {

		private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

		private static final long OFFSET;

		static {
			long offset = -1L;
			try {
				offset = UNSAFE.objectFieldOffset(Reference.class.getDeclaredField("referent"));
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			OFFSET = offset;
		}

		/**
		 * 节点的版本
		 * <p>
		 * 当且仅当目标版本与节点版本相同时，节点中的键值对才是有效的。
		 */
		int version;
		/**
		 * 节点中的值
		 * <p>
		 * 当且仅当目标版本与节点版本相同时，节点中的键值对才是有效的。
		 */
		private int value;

		public WeakNode() {
			this(0, null, -1);
		}

		/**
		 * 指定节点版本和存放键值对的构造方法
		 *
		 * @param version 容器版本
		 * @param key     节点中的键
		 * @param value   节点中的值
		 */
		public WeakNode(int version, Object key, int value) {
			// ReferenceQueue必须传入null。当对象回收后，JVM会将队列设置成ReferenceQueue.ENQUEUE
			// 或ReferenceQueue.NULL，如果需要利用这个特性，需要重新设置队列。
			super(key, null);
			this.version = version;
			this.value = value;
		}

		/**
		 * 设置节点中的节点版本和映射对象
		 *
		 * @param version 容器版本
		 * @param key     节点中的键
		 * @param value   节点中的值
		 */
		public void set(int version, Object key, int value) {
			this.version = version;
			this.value = value;
			UNSAFE.putObject(this, OFFSET, key);
		}

		/**
		 * 判断节点是否为空
		 *
		 * @param version 目标版本
		 * @return {@code true}空，{@code false}非空
		 */
		public boolean isEmpty(int version) {
			return this.version != version;
		}

		/**
		 * 判断节点是否非空
		 *
		 * @param version 目标版本
		 * @return {@code true}非空，{@code false}空
		 */
		public boolean isNotEmpty(int version) {
			return this.version == version;
		}

		@Override
		public Object getKey() {
			return get();
		}

		@Override
		public int getValue() {
			return value;
		}
	}
}