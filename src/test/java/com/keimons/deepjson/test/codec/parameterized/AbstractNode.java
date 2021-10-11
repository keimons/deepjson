package com.keimons.deepjson.test.codec.parameterized;

import java.util.Map;

/**
 * 测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AbstractNode<T1, T2, T3 extends Number> {

	public Map<String, Integer> value0;

	public T1 value1;

	public T2 value2;

	public T3 value3;
}