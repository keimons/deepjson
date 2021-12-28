package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;

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
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, T value, int uniqueId, long options) throws IOException {
		if (model == CodecModel.V || CodecOptions.PrimitiveKey.isOptions(options)) {
			encode0(writer, value);
		} else {
			writer.writeMark('"');
			encode0(writer, value);
			writer.writeMark('"');
		}
	}

	@Override
	public T decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		SyntaxToken token = reader.token();
		if (token == SyntaxToken.NULL) {
			return null;
		}
		return decode0(context, reader, clazz, options);
	}

	protected abstract void encode0(JsonWriter writer, T value) throws IOException;

	protected abstract T decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options);
}