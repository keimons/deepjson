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
public abstract class BasePrimitiveCodec<T> extends AbstractClassCodec<T> {

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, T value, int uniqueId, long options) {
		if (model == CodecModel.V || CodecOptions.PrimitiveKey.isOptions(options)) {
			encode0(buf, value);
		} else {
			buf.writeMark('"');
			encode0(buf, value);
			buf.writeMark('"');
		}
	}

	@Override
	public T decode(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.NULL) {
			return null;
		}
		return decode0(context, buf, clazz, options);
	}

	protected abstract void encode0(AbstractBuffer buf, T value);

	protected abstract T decode0(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options);
}