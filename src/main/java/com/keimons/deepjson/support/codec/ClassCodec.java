package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.util.ClassUtil;

import java.io.IOException;

/**
 * {@link Class}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassCodec extends AbstractOnlineCodec<Class<?>> {

	public static final ClassCodec instance = new ClassCodec();

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Class<?> value, int uniqueId, long options) throws IOException {
		writer.writeWithQuote(value.getName());
	}

	@Override
	public Class<?> decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		reader.assertExpectedSyntax(SyntaxToken.STRING);
		return ClassUtil.findClass(reader.stringValue());
	}
}