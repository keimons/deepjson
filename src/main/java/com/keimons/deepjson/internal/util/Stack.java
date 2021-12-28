package com.keimons.deepjson.internal.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * 出栈顺序迭代的堆栈
 * <p>
 * {@code Stack}表示对象后进先出(LIFO)的堆栈。将数组视为堆栈，提供了
 * 常用的{@code push}和{@code poll}操作，以及{@code peek}获取栈
 * 顶元素的方法，测试栈是否为{@code isEmpty}的方法。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Stack<E> implements Iterable<E> {

	private Object[] elements = new Object[16];

	private int count;

	@SuppressWarnings("unchecked")
	private E elementAt(int index) {
		return (E) elements[index];
	}

	@SuppressWarnings("unchecked")
	private E elementAt(int index, E e) {
		if (index == elements.length) {
			Object[] newElements = new Object[elements.length << 1];
			System.arraycopy(elements, 0, newElements, 0, elements.length);
			elements = newElements;
		}
		E old = (E) elements[index];
		elements[index] = e;
		return old;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	public int size() {
		return count;
	}

	/**
	 * 将一个对象压入堆栈
	 *
	 * @param e 入栈对象
	 */
	public void push(E e) {
		elementAt(count++, e);
	}

	/**
	 * 返回栈顶元素，但并不是移除它。
	 *
	 * @return 栈顶元素
	 */
	public E peek() {
		return elementAt(count - 1);
	}

	/**
	 * 移除并返回栈顶元素
	 *
	 * @return 栈顶元素
	 */
	public E poll() {
		return elementAt(--count, null);
	}

	/**
	 * 清空栈
	 */
	public void clear() {
		for (int i = 0; i < count; i++) {
			elementAt(i, null);
		}
	}

	@NotNull
	@Override
	public Iterator<E> iterator() {
		return new ReverseIterator();
	}

	/**
	 * 倒序迭代器
	 * <p>
	 * 这个栈的实现主要是在迭代器时，按照出栈顺序进行迭代。
	 */
	private class ReverseIterator implements Iterator<E> {

		int index;

		@Override
		public boolean hasNext() {
			return index < count;
		}

		@Override
		public E next() {
			index++;
			return elementAt(count - index);
		}
	}
}