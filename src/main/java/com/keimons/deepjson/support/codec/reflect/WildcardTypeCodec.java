package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.codec.BaseCodec;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * {@link WildcardType}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class WildcardTypeCodec extends BaseCodec<Object> {

	public static final WildcardTypeCodec instance = new WildcardTypeCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		assert type instanceof WildcardType;
		WildcardType wildcardType = (WildcardType) type;
		// 优先匹配 下界通配符
		Type[] lowerBounds = wildcardType.getLowerBounds();
		if (lowerBounds.length > 0 && lowerBounds[0] != Object.class) {
			return decode0(context, buf, lowerBounds[0], options);
		}
		// 上界通配符
		Type[] upperBounds = wildcardType.getUpperBounds();
		if (upperBounds.length > 0 && upperBounds[0] != Object.class) {
			return decode0(context, buf, upperBounds[0], options);
		}
		// 上界或者下届通配符且只有1个，那么必然是Object.class
		if (upperBounds.length > 0 || lowerBounds.length > 0) {
			return decode0(context, buf, Object.class, options);
		}
		// 无法解析 上下界均为空
		throw new TypeNotFoundException("unknown wildcard type " + type.getTypeName());
	}

	private Object decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		ICodec<?> codec = CodecFactory.getCodec(type);
		assert codec != null;
		return codec.decode(context, buf, type, options);
	}
}