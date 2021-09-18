package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;

/**
 * 基础类型数组编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class BasePrimitiveCodec<T> extends BaseCodec<T> {

	@Override
	public boolean isSearch() {
		return false;
	}

	public void writeKey(AbstractContext context, AbstractBuffer buf, T value, int uniqueId, long options) {
		buf.writeMark('"');
		encode(context, buf, value, uniqueId, options);
		buf.writeMark('"');
	}
}