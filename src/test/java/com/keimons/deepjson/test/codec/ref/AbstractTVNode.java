package com.keimons.deepjson.test.codec.ref;

import com.keimons.deepjson.test.Node;

import java.io.Serializable;
import java.util.Map;

/**
 * 测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AbstractTVNode<T0, T1, T2, T3 extends Number, T4 extends Map<String, Node> & Serializable, T5 extends Node & Serializable> {

	public T0 value0;

	public T1 value1;

	public T2 value2;

	public T3 value3;

	public T4 value4;

	public T5 value5;
}