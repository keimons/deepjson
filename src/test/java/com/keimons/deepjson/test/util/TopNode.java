package com.keimons.deepjson.test.util;

import java.util.Map;

/**
 * 顶部测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TopNode<T extends PT1Node, V> {

	public T topValue0;

	public T[] topValue1;

	public Map<? extends T, ? super T> topValue2;

	public V topValue10;

	public V[] topValue11;

	public InnerNode<T> value;
}