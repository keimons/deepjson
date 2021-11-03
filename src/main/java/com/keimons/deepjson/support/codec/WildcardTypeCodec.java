package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * {@link WildcardType}通配符编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class WildcardTypeCodec extends AbstractPhantomCodec {

	public static final WildcardTypeCodec instance = new WildcardTypeCodec();

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public Object decode(ReaderContext context, ReaderBuffer buf, Type type, long options) {
		assert type instanceof WildcardType;
		WildcardType wildcardType = (WildcardType) type;
		// 优先匹配 下界通配符
		Type[] lowerBounds = wildcardType.getLowerBounds();
		if (lowerBounds.length > 0) {
			return decode0(context, buf, lowerBounds[0], options);
		}
		// 上界通配符
		Type[] upperBounds = wildcardType.getUpperBounds();
		if (upperBounds.length > 0) {
			return decode0(context, buf, upperBounds[0], options);
		}
		// 无法解析 上下界均为空
		throw new TypeNotFoundException("unknown wildcard type " + type.getTypeName());
	}

	private Object decode0(ReaderContext context, ReaderBuffer buf, Type type, long options) {
		ICodec<?> codec = CodecFactory.getCodec(type);
		assert codec != null;
		return codec.decode(context, buf, type, options);
	}
}