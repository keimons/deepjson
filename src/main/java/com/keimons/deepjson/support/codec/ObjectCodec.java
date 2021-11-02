package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.UnsupportedException;

import java.util.Map;

/**
 * {@link Object}编解码器
 * <p>
 * {@link Object}编解码器是一个特殊编解码。它并不能解析成真正的{@link Object}对象，而是采用
 * 自适应的策略，解析成一个对象的对象。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ObjectCodec extends AbstractClassCodec<Object> {

	public static final ICodec<Object> instance = new ObjectCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		buf.writeMark('{');
		buf.writeMark('}');
	}

	@Override
	public Object decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		SyntaxToken token = buf.token();
		switch (token) {
			case NULL:
				return null;
			case TRUE:
				return Boolean.TRUE;
			case FALSE:
				return Boolean.FALSE;
			case STRING:
				return buf.stringValue();
			case NUMBER:
				return buf.adaptiveNumber();
			case LBRACE:
				return context.decode(buf, Map.class, options);
			case LBRACKET:
				return JsonArrayCodec.instance.decode(context, buf, clazz, options);
			default:
				throw new UnsupportedException();
		}
	}
}