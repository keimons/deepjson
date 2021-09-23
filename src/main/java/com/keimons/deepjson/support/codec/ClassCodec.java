package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.reflect.Type;

/**
 * {@link Class}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ClassCodec extends BaseCodec<Class<?>> {

	public static final ClassCodec instance = new ClassCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Class<?> value, int uniqueId, long options) {
		buf.writeWithQuote(value.getName());
	}

	@Override
	public Class<?> decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(SyntaxToken.STRING);
		return ClassUtil.findClass(buf.stringValue());
	}
}