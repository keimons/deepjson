package com.keimons.deepjson.test.codec.collection;

/**
 * 测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Node {

	int value0;

	Node value1;

	public Node() {
	}

	public Node(int value0) {
		this.value0 = value0;
	}

	@Override
	public int hashCode() {
		return value0;
	}
}