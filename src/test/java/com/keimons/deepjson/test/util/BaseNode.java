package com.keimons.deepjson.test.util;

/**
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public abstract class BaseNode {

	public int value0;

	public long value1;

	public float value2;

	public int getValue0() {
		return value0;
	}

	public void setValue0(int value0) {
		this.value0 = value0;
	}

	public long getValue1() {
		return value1;
	}

	public void setValue1(long value1) {
		this.value1 = value1;
	}

	public float getValue2() {
		return value2;
	}

	public void setValue2(float value2) {
		this.value2 = value2;
	}
}