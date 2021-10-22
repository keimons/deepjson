package com.keimons.deepjson.support;

import java.util.Collection;
import java.util.Map;

/**
 * 元素数量计数器
 * <p>
 * 使用深度优先算法遍历{@link Collection}对象、{@link Map}对象和{@code Object[]}对象时，
 * 有可能对象正在改变。所以，我们会依次记录原对象-&lt;计数器-&lt;子节点，统计当前瞬间的节点数量后，才能
 * 确定真实的节点写入数量。此节点仅仅包含一个计数器，主要起到占位的作用。
 * <p>
 * 写入缓冲区时，先取出原对象，再取出计数器，根据计数器中的值，读取x个对象，进行编码。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ElementsFuture {

	/**
	 * 元素数量
	 */
	private int count;

	public ElementsFuture() {

	}

	public ElementsFuture(int count) {
		this.count = count;
	}

	/**
	 * 增加元素数量
	 */
	public void addCount() {
		count++;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}