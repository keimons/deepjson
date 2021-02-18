package com.keimons.deepjson;

public class NormalNode implements INode {

	private byte node1 = 'c';

	private short node2 = 23;

	private int node3 = 10000;

	private long node4 = 10000L;

	private boolean node5 = true;

	private char 我 = 'c';

//	private float node7 = 1234.5678f;
//
//	private double node8 = 1234.5678d;

	public byte getNode1() {
		return node1;
	}

	public void setNode1(byte node1) {
		this.node1 = node1;
	}

	public short getNode2() {
		return node2;
	}

	public void setNode2(short node2) {
		this.node2 = node2;
	}

	public int getNode3() {
		return node3;
	}

	public void setNode3(int node3) {
		this.node3 = node3;
	}

	public long getNode4() {
		return node4;
	}

	public void setNode4(long node4) {
		this.node4 = node4;
	}

	public boolean isNode5() {
		return node5;
	}

	public void setNode5(boolean node5) {
		this.node5 = node5;
	}

	public char get我() {
		return 我;
	}

	public void set我(char 我) {
		this.我 = 我;
	}

//	public float getNode7() {
//		return node7;
//	}
//
//	public void setNode7(float node7) {
//		this.node7 = node7;
//	}
//
//	public double getNode8() {
//		return node8;
//	}
//
//	public void setNode8(double node8) {
//		this.node8 = node8;
//	}
}