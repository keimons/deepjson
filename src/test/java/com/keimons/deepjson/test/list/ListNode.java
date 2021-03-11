package com.keimons.deepjson.test.list;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link List}测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ListNode {

	private List<Short> value = new LinkedList<>();

	public List<Short> getValue() {
		return value;
	}

	public void setValue(List<Short> value) {
		this.value = value;
	}
}