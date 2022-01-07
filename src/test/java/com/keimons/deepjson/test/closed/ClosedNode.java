package com.keimons.deepjson.test.closed;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 不开放测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClosedNode extends GenericNode<String> {

	private static final AtomicInteger ID;

	static {
		ID = new AtomicInteger();
	}

	public ClosedNode() {
		super(null);
	}

	public ClosedNode(boolean value0) {
		super(null);
	}

	public ClosedNode(final char value1) {
		super(null);
	}

	public ClosedNode(byte value2) {
		super(null);
	}

	public ClosedNode(final short value3) {
		super(null);
	}

	public ClosedNode(int value4) {
		super(null);
	}

	public ClosedNode(final long value5) {
		super(null);
	}

	public ClosedNode(float value6) {
		super(null);
	}

	public ClosedNode(final double value7) {
		super(null);
	}

	public ClosedNode(String name) {
		super(name);
	}

	public ClosedNode(String name, int level) {
		super(name);
	}

	public class InnerClass0 {

		public InnerClass0() {

		}

		public InnerClass0(String name) {

		}

		public InnerClass0(Object value0, String value1, Object value2, String value3) {

		}
	}

	public static class InnerClass1 {

		public InnerClass1() {

		}

		public InnerClass1(String name) {

		}

		public InnerClass1(Object value0, String value1, Object value2, String value3) {

		}
	}
}