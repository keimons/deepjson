package com.keimons.deepjson.test;

/**
 * 测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Node {

	private boolean value0 = true;

	private byte value1 = 111;

	private short value2 = 222;

	private int value3 = 333;

	private long value4 = 444;

	public static Node of() {
		return new Node();
	}

	public static Node[] array1() {
		return new Node[]{new Node()};
	}

	public static Node[][] array2() {
		Node[][] value = new Node[1][];
		value[0] = new Node[]{new Node()};
		return value;
	}
}