package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.util.UnsupportedException;

import java.io.IOException;

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
public class ObjectCodec extends KlassCodec<Object> {

	public static final ICodec<Object> instance = new ObjectCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Object value, int uniqueId, long options) throws IOException {
		writer.writeMark('{');
		writer.writeMark('}');
	}

	@Override
	public Object decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		SyntaxToken token = reader.token();
		switch (token) {
			case NULL:
				return null;
			case TRUE:
				return Boolean.TRUE;
			case FALSE:
				return Boolean.FALSE;
			case STRING:
				return reader.stringValue();
			case NUMBER:
				return reader.adaptiveNumber();
			case LBRACE:
			case LBRACKET:
				return context.decode(reader, Json.class, options);
			default:
				throw new UnsupportedException();
		}
	}
}