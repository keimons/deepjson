package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

/**
 * 基础类型数组编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractPrimitiveCodec<T> extends KlassCodec<T> {

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, T value, int uniqueId, long options) {
		if (model == CodecModel.V || CodecOptions.PrimitiveKey.isOptions(options)) {
			encode0(buf, value);
		} else {
			buf.writeMark('"');
			encode0(buf, value);
			buf.writeMark('"');
		}
	}

	@Override
	public T decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.NULL) {
			return null;
		}
		return decode0(context, buf, clazz, options);
	}

	protected abstract void encode0(WriterBuffer buf, T value);

	protected abstract T decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options);
}