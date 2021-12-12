package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;

import java.io.IOException;
import java.util.Collection;

/**
 * 已知类型的编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypedCollectionCodec<T> extends AbstractOnlineCodec<Collection<T>> {

	final Class<T> clazz;

	ICodec<T> codec;

	public TypedCollectionCodec(Class<T> clazz) {
		this.clazz = clazz;
		this.codec = CodecFactory.getCodec(clazz);
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Collection<T> values, int uniqueId, long options) throws IOException {
		char mark = '{';
		// write class name
		boolean className = CodecOptions.WriteClassName.isOptions(options);
		if (className) {
			Class<?> clazz = values.getClass();
			if (CodecConfig.WHITE_COLLECTION.contains(clazz)) {
				writer.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0) {
			writer.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		if (uniqueId >= 0 || className) {
			writer.writeName(mark, FIELD_VALUE);
		}
		mark = '[';
		writer.writeMark('[');
		for (T value : values) {
			writer.writeMark(mark);
			codec.encode(context, writer, model, value, uniqueId, options);
			mark = ',';
		}
		writer.writeMark(']');
		if (uniqueId >= 0 || className) {
			writer.writeMark('}');
		}
	}

	@Override
	protected Collection<T> decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		throw new UnsupportedOperationException();
	}
}