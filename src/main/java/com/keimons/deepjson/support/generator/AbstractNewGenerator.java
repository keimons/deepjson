package com.keimons.deepjson.support.generator;

import com.keimons.deepjson.Generator;
import com.keimons.deepjson.util.WriteFailedException;

/**
 * 字符串生成器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractNewGenerator<T> extends Generator<T> {

	/**
	 * 构造方法
	 */
	public AbstractNewGenerator() {
		super(null, null);
	}

	@Override
	public final T generate(T dest, int offset, char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException {
		return generate(buffers, length, bufferIndex, writeIndex);
	}

	protected abstract T generate(char[][] buffers, int length, int bufferIndex, int writeIndex) throws WriteFailedException;
}