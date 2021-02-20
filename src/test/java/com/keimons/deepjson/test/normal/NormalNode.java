package com.keimons.deepjson.test.normal;

import com.keimons.deepjson.test.INode;

public class NormalNode implements INode {

	private byte node1 = 'k';

	private short node2 = 23;

	private int node3 = 10000;

	private long node4 = 10000L;

	private boolean node5 = true;

	private char node6 = 'k';

	private float node7 = 1234.5678f;

	private double node8 = 1234.5678d;

	private transient String node9 = "蒙奇";

	private LinkNode node10 = new LinkNode();

	private int[] node11 = new int[10];

	private Integer[] node12 = new Integer[10];

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

	public char getNode6() {
		return node6;
	}

	public void setNode6(char node6) {
		this.node6 = node6;
	}

	public float getNode7() {
		return node7;
	}

	public void setNode7(float node7) {
		this.node7 = node7;
	}

	public double getNode8() {
		return node8;
	}

	public void setNode8(double node8) {
		this.node8 = node8;
	}

	public String getNode9() {
		return node9;
	}

	public void setNode9(String node9) {
		this.node9 = node9;
	}

	public LinkNode getNode10() {
		return node10;
	}

	public void setNode10(LinkNode node10) {
		this.node10 = node10;
	}

	public int[] getNode11() {
		return node11;
	}

	public void setNode11(int[] node11) {
		this.node11 = node11;
	}

	public Integer[] getNode12() {
		return node12;
	}

	public void setNode12(Integer[] node12) {
		this.node12 = node12;
	}
}