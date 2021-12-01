package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.util.ClassUtil;

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
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Class<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.getName());
	}

	@Override
	public Class<?> decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(SyntaxToken.STRING);
		return ClassUtil.findClass(buf.stringValue());
	}
}