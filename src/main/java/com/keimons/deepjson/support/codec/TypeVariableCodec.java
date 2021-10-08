package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * {@link TypeVariable}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class TypeVariableCodec extends AbstractReflectCodec {

	public static final TypeVariableCodec instance = new TypeVariableCodec();

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		assert type instanceof TypeVariable;
		TypeVariable<?> tv = (TypeVariable<?>) type;
		Type instanceType = context.findInstanceType(tv);
		if (instanceType instanceof TypeVariable) {
			// 泛型已经不能被解析，所以这里实际上是在使用上边界判定
			Type[] bounds = ((TypeVariable<?>) type).getBounds(); // class(ParameterizedType) interface
			if (bounds.length == 1) {
				return context.decode(buf, bounds[0], options);
			}
			// more than one
			if (ClassUtil.check(bounds)) {
				// 介于java是单继承，只有第一个可能是类，其它的必然是接口或接口类型ParameterizedType
				// 0位置 是class或ParameterizedType
				instanceType = bounds[0];
			} else {
				instanceType = CodecConfig.getType(bounds);
				if (instanceType == null) {
					throw new IncompatibleTypeException("unknown type bounds " + Arrays.toString(bounds));
				}
			}
		}
		return context.decode(buf, instanceType, options);
	}
}