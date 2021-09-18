package com.keimons.deepjson.pool;

import com.keimons.deepjson.ICharBuffer;
import com.keimons.deepjson.util.SimpleReference;

/**
 * 缓存缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SoftCharBuffer extends SimpleReference<char[]> implements ICharBuffer {

	int length;

	/**
	 * 确保在使用中，对象不会被回收
	 */
	char[] strong;

	public SoftCharBuffer(int length) {
		super(new char[length]);
		this.length = length;
	}

	@Override
	public boolean isCached() {
		return true;
	}

	@Override
	public void mark() {
		strong = get(); // 增加一个强引用
		if (strong == null) { // 对象已经被回收，重新设置一个值
			strong = new char[length];
			super.set(strong);
		}
	}

	@Override
	public void release() {
		strong = null; // 释放强引用
	}
}