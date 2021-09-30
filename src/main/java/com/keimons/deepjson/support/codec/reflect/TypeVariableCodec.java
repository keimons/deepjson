package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.codec.BaseCodec;

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
public class TypeVariableCodec extends BaseCodec<Object> {

	public static final TypeVariableCodec instance = new TypeVariableCodec();

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
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
			instanceType = Config.getType(bounds); // 介于java是单继承，只有第一个可能是类，其它的必然是接口
			if (instanceType == null) {
				throw new IncompatibleTypeException("unknown type bounds " + Arrays.toString(bounds));
			}
		}
		return context.decode(buf, instanceType, options);
	}

	/**
	 * 检测类型
	 *
	 * @param context 上下文信息
	 * @param target  类型信息
	 * @param bounds  边界信息
	 * @return {@code true}检测成功，{@code false}检测失败
	 */
	private boolean check(IDecodeContext context, Class<?> target, Type[] bounds) {
		boolean check = true;
		for (int i = 1; i < bounds.length; i++) {
			Class<?> clazz = context.findInstanceType(bounds[i]);
			if (!clazz.isAssignableFrom(target)) {
				check = false;
				break;
			}
		}
		if (!check) {
			check = true;
//			target = Config.DEFAULT.get(target);
			if (target == null) {
				return false;
			}
			for (int i = 1; i < bounds.length; i++) {
				Class<?> clazz = context.findInstanceType(bounds[i]);
				if (!clazz.isAssignableFrom(target)) {
					check = false;
					break;
				}
			}
		}
		return check;
	}
}