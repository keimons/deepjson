package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.support.codec.ObjectArrayCodec;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

/**
 * {@code T[]}编解码器
 * <p>
 * 同时这也是一个{@link GenericArrayType}专用解码器。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class GenericArrayTypeCodec extends ObjectArrayCodec {

	public static final GenericArrayTypeCodec instance = new GenericArrayTypeCodec();

	@Override
	protected Type findComponentType(IDecodeContext context, Type type) {
		return ((GenericArrayType) type).getGenericComponentType();
	}
}