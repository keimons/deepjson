package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ReflectUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * {@code T[]}编解码器
 * <p>
 * 同时这也是一个{@link GenericArrayType}专用解码器。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class GenericArrayTypeCodec extends AbstractReflectCodec {

	public static final GenericArrayTypeCodec instance = new GenericArrayTypeCodec();

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		assert type instanceof GenericArrayType;
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.LBRACKET) {
			Class<?> clazz = context.findInstanceType(type, Object[].class);
			ICodec<?> codec = CodecFactory.getCodec(clazz);
			assert codec != null;
			return codec.decode(context, buf, clazz, options);
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> excepted = typeCheck(context, buf, options);
		Class<?> clazz = context.findInstanceType(type, excepted);
		if (excepted != null) {
			if (!Object[].class.isAssignableFrom(excepted)) { // 必须是 对象数组类型 或 子类
				throw new IncompatibleTypeException(excepted, Object[].class);
			}
			if (!clazz.isAssignableFrom(excepted)) {
				throw new IncompatibleTypeException(excepted, Object[].class);
			}
			clazz = excepted;
			token = buf.nextToken();
		}
		assert clazz.isArray();
		int uniqueId = -1;
		Object value = null;
		for (; ; ) {
			// 断言当前位置一定是一个对象
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 判断是否 "@id"
			if (token == SyntaxToken.STRING && buf.checkPutId()) {
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.COLON); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.NUMBER, SyntaxToken.STRING);
				uniqueId = buf.intValue();
			} else if (token == SyntaxToken.STRING && buf.checkGetValue()) {
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.COLON); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
				Type componentType = ((GenericArrayType) type).getGenericComponentType();
				if (componentType instanceof TypeVariable) {
					value = ObjectArrayCodec.instance.decode0(context, buf, clazz.getComponentType(), clazz.getComponentType(), options);
				} else if (componentType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) componentType;
					pt = ReflectUtil.makeParameterizedType(pt.getOwnerType(), clazz.getComponentType(), pt.getActualTypeArguments());
					value = ObjectArrayCodec.instance.decode0(context, buf, clazz.getComponentType(), pt, options);
				} else if (componentType instanceof GenericArrayType) {
					GenericArrayType gat = (GenericArrayType) componentType;
					value = ObjectArrayCodec.instance.decode0(context, buf, clazz.getComponentType(), gat, options);
				} else {
					throw new IncompatibleTypeException(type);
				}
			} else {
				throw new UnknownSyntaxException("array error");
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			token = buf.nextToken();
		}
		if (uniqueId != -1) {
			context.put(uniqueId, value);
		}
		return value;
	}
}