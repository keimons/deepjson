package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.test.Node;

import java.io.Serializable;
import java.util.Map;

/**
 * 泛型数组节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AbstractGenericArrayNode<T0, T1 extends Node, T2 extends Node & Serializable, T3 extends Map<String, Integer> & Serializable> {

	Map<String, T0>[] value0;

	Map<String, T0>[][] value1;

	T0[] value00;

	T0[][] value01;

	T1[] value10;

	T1[][] value11;

	T2[] value20;

	T2[][] value21;

	T3[] value30;

	T3[][] value31;
}