package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;

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

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.NULL) {
			return null;
		}
		return decode0(context, buf, type, options);
	}

	protected abstract Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options);
}