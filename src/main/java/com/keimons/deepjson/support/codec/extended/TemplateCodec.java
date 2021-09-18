package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 预热模板
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TemplateCodec extends ExtendedCodec {

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Object value, int uniqueId, long options) {

	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		Class<?> clazz;
		if (type instanceof ParameterizedType) {
			clazz = (Class<?>) ((ParameterizedType) type).getRawType();
		} else {
			clazz = (Class<?>) type;
		}
		Field value0 = null;
		Field value1 = null;
		try {
			value0 = clazz.getField("value0");
			value1 = clazz.getField("value1");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		Object o = null;
		try {
			o = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (; ; ) {
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.STRING);
			int hashcode = buf.valueHashcode();
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.COLON);
			buf.nextToken();
			switch (hashcode) {
				case 63690784:
					Type t1 = context.findType(value0);
					try {
						value0.set(o, context.decode(buf, t1, options, false));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				case 63690785:
					Type t2 = context.findType(value1);
					try {
						value1.set(o, context.decode(buf, t2, options, false));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				case -823812895:
					break;
				case -823812894:
					break;
				case -823812893:
					break;
				case -823812892:
					break;
				case -823812891:
					break;
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
		}
		return o;
	}
}