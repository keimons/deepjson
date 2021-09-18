package com.keimons.deepjson.util;

import org.jetbrains.annotations.Nullable;

/**
 * 将一个对象映射为一个{@code int}值
 * <p>
 * 这是一个定制化的映射，因为它的{@code value}始终为{@code int}值。当构造映
 * 射容器时，需要指定一个默认值。{@link #get(Object)}当获取映射失败时，不能
 * 返回{@code null}，而是返回{@link #defaultValue}。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractMap<K> {

	/**
	 * 默认值
	 */
	protected final int defaultValue;

	public AbstractMap(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * 获取最大容量
	 *
	 * @return 最大容量
	 */
	public abstract int capacity();

	/**
	 * 键值关联
	 * <p>
	 * 如果指定的键尚未与{@code int}值关联（或关联到{@link #defaultValue}）则将其与给定值关联
	 * 并返回{@link #defaultValue}，否则将键关联到新值，并返回旧值。
	 * <p>
	 * {@code null}将始终映射为{@link #defaultValue}并且不可修改。
	 *
	 * @param key   键
	 * @param value 值
	 * @return 键不存在时返回{@link #defaultValue}，键存在时返回{@code oldValue}。
	 */
	public abstract int put(@Nullable K key, int value);

	/**
	 * 当且仅当给定的键没有关联时，才进行键值关联
	 * <p>
	 * 如果指定的键尚未与{@code int}值关联（或关联到{@link #defaultValue}）则将其与给定值关联
	 * 并返回{@link #defaultValue}，否则返回当前值。
	 * <p>
	 * {@code null}将始终映射为{@link #defaultValue}不可修改。
	 *
	 * @param key      键
	 * @param newValue 值
	 * @return 关联成功时返回{@link #defaultValue}，关联失败时返回{@code oldValue}。
	 */
	public abstract int putIfAbsent(@Nullable K key, int newValue);

	/**
	 * 获取与指定的键关联的值
	 * <p>
	 * {@code null}将始终映射为{@link #defaultValue}不可修改。
	 *
	 * @param key 键
	 * @return 值
	 */
	public abstract int get(@Nullable K key);

	/**
	 * 获取与指定的键关联的值，当映射不存在时，返回{@link #defaultValue}。
	 * <p>
	 * {@code null}将始终映射为{@link #defaultValue}不可修改。
	 *
	 * @param key          键
	 * @param defaultValue 默认值
	 * @return 当映射存在时返回对应值，当映射不存在时返回{@link #defaultValue}。
	 */
	public abstract int getOrDefault(@Nullable K key, int defaultValue);

	/**
	 * 清空映射
	 */
	abstract void clear();

	/**
	 * 存放键值对的节点
	 *
	 * @param <K> 映射中的键类型
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 **/
	interface INode<K> {

		/**
		 * 获取映射中的键
		 *
		 * @return 映射中的键
		 */
		K getKey();

		/**
		 * 获取映射中的值
		 *
		 * @return 映射中的值
		 */
		int getValue();
	}
}