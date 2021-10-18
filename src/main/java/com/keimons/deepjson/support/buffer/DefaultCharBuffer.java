package com.keimons.deepjson.support.buffer;

/**
 * 缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DefaultCharBuffer implements CharBuffer {

	private char[] cache;

	public DefaultCharBuffer(int length) {
		cache = new char[length];
	}

	@Override
	public void set(char[] buf) {
		cache = buf;
	}

	@Override
	public char[] get() {
		return cache;
	}

	@Override
	public boolean isCached() {
		return false;
	}

	@Override
	public void mark() {

	}

	@Override
	public void release() {
		cache = null;
	}
}