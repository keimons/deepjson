package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ElementsFuture;

/**
 * 空编解码器方案
 * <p>
 * 对象和节点始终保持一一对应，但是，对于一些对象，是不需要需要编解码方案的，
 * 主要用于{@link ElementsFuture}计数器的占位。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class EmptyCodec extends AbstractClassCodec<ElementsFuture> {

	public static EmptyCodec instance = new EmptyCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void build(WriterContext context, ElementsFuture value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ElementsFuture decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, ElementsFuture value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
	}
}