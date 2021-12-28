package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ReflectUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * {@code T[]}和{@code Map&lt;String, Integer&gt;[]}泛型数组编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class GenericArrayTypeCodec extends PhantomCodec {

	public static final GenericArrayTypeCodec instance = new GenericArrayTypeCodec();

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public Object decode(ReaderContext context, JsonReader reader, Type type, long options) {
		assert type instanceof GenericArrayType;
		SyntaxToken token = reader.token();
		if (token == SyntaxToken.LBRACKET) {
			Class<?> clazz = context.findInstanceType(type, null);
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			if (componentType instanceof TypeVariable) {
				return ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), componentType, options);
			} else if (componentType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) componentType;
				pt = ReflectUtil.makeParameterizedType(pt.getOwnerType(), clazz.getComponentType(), pt.getActualTypeArguments());
				return ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), pt, options);
			} else if (componentType instanceof GenericArrayType) {
				GenericArrayType gat = (GenericArrayType) componentType;
				return ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), gat, options);
			} else {
				throw new IncompatibleTypeException(type);
			}
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = reader.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> excepted = typeCheck(context, reader, options);
		Class<?> clazz = context.findInstanceType(type, excepted);
		if (excepted != null) {
			if (!Object[].class.isAssignableFrom(excepted)) { // 必须是 对象数组类型 或 子类
				throw new IncompatibleTypeException(excepted, Object[].class);
			}
			if (!clazz.isAssignableFrom(excepted)) {
				throw new IncompatibleTypeException(excepted, Object[].class);
			}
			clazz = excepted;
			token = reader.nextToken();
		}
		assert clazz.isArray();
		int uniqueId = -1;
		Object value = null;
		for (; ; ) {
			// 断言当前位置一定是一个对象
			reader.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 判断是否 "@id"
			if (token == SyntaxToken.STRING && reader.checkPutId()) {
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.COLON); // 预期当前语法是 ":"
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.NUMBER, SyntaxToken.STRING);
				uniqueId = reader.intValue();
			} else if (token == SyntaxToken.STRING && reader.checkGetValue()) {
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.COLON); // 预期当前语法是 ":"
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
				Type componentType = ((GenericArrayType) type).getGenericComponentType();
				if (componentType instanceof TypeVariable) {
					value = ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), clazz.getComponentType(), options);
				} else if (componentType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) componentType;
					pt = ReflectUtil.makeParameterizedType(pt.getOwnerType(), clazz.getComponentType(), pt.getActualTypeArguments());
					value = ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), pt, options);
				} else if (componentType instanceof GenericArrayType) {
					GenericArrayType gat = (GenericArrayType) componentType;
					value = ObjectArrayCodec.instance.decode0(context, reader, clazz.getComponentType(), gat, options);
				} else {
					throw new IncompatibleTypeException(type);
				}
			} else {
				throw new UnknownSyntaxException("array error");
			}
			token = reader.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			token = reader.nextToken();
		}
		if (uniqueId != -1) {
			context.put(uniqueId, value);
		}
		return value;
	}
}