package com.keimons.deepjson.support.codec.reflect;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.codec.BaseCodec;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
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
		if (instanceType == null) {
			throw new TypeNotFoundException("unknown TypeVariable type " + type.getTypeName());
		}
		if (instanceType instanceof Class) {
			return context.decode(buf, instanceType, options);
		}
		if (instanceType instanceof TypeVariable) {
			// 泛型已经不能被解析，所以这里实际上是在使用上边界判定
			Type[] bounds = ((TypeVariable<?>) type).getBounds(); // class(ParameterizedType) interface
			// we have no idea of type variable
			if (bounds.length <= 0) {
				if (false) { // TODO 设计兼容模式 和 严格模式
					throw new TypeNotFoundException("");
				}
				return context.decode(buf, Object.class, options);
			}
			if (bounds.length == 1) {
				return context.decode(buf, bounds[0], options);
			}
			SyntaxToken token = buf.nextToken();
			// 边界参数泛型
			if (token == SyntaxToken.STRING && buf.checkGetType()) {
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.COLON);
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.STRING);
				String className = buf.stringValue();
				Class<?> clazz = ClassUtil.findClass(className); // 解析类中的名字
				for (Type bound : bounds) {
					Class<?> parent;
					if (bound instanceof Class<?>) {
						parent = (Class<?>) bound;
					} else if (bound instanceof ParameterizedType) {
						parent = (Class<?>) ((ParameterizedType) bound).getRawType();
					} else {
						String msg = Arrays.toString(bounds);
						throw new MalformedParameterizedTypeException(msg);
					}
					if (!parent.isAssignableFrom(clazz)) {
						throw new IncompatibleTypeException(bound, clazz);
					}
				}
				return context.decode(buf, clazz, options);
			}
		}
		return context.decode(buf, instanceType, options);
	}
}