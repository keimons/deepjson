package com.keimons.deepjson.support;

/**
 * 引用指向另一个节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReferenceNode {

	private int unique;

	public ReferenceNode(int unique) {
		this.unique = unique;
	}

	public int getUnique() {
		return unique;
	}

	public void setUnique(int unique) {
		this.unique = unique;
	}
}