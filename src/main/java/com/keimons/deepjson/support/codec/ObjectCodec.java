package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.UnsupportedException;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link Object}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ObjectCodec extends BaseCodec<Object> {

	public static final ICodec<Object> instance = new ObjectCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		buf.writeMark('{');
		buf.writeMark('}');
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
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
				return context.decode(buf, Map.class, false, options);
			case LBRACKET:
				return JsonArrayCodec.instance.decode(context, buf, type, options);
			default:
				throw new UnsupportedException();
		}
	}
}