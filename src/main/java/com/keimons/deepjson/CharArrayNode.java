package com.keimons.deepjson;

import com.keimons.deepjson.util.ArrayUtil;

/**
 * 字节数组节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class CharArrayNode {

	char[] values;

	int hashcode;

	public CharArrayNode(char[] values) {
		this.values = values;
		this.hashcode = ArrayUtil.hashcode(values);
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReaderBuffer) {
			return ((ReaderBuffer) obj).isSame(values);
		} else {
			return this.equals(obj);
		}
	}
}