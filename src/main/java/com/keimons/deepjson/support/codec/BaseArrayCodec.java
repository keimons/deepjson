package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.reflect.Type;

/**
 * 基础数据类型数组编解码器父类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see BooleanArrayCodec boolean[]
 * @see ByteArrayCodec byte[]
 * @see CharArrayCodec char[]
 * @see DoubleArrayCodec double[]
 * @see FloatArrayCodec float[]
 * @see ShortArrayCodec short[]
 * @see IntegerArrayCodec int[]
 * @see LongArrayCodec long[]
 * @since 1.6
 **/
public abstract class BaseArrayCodec<T> extends BaseCodec<T> {

	private final Type clazz;

	public BaseArrayCodec() {
		clazz = ClassUtil.findGenericType(this.getClass(), BaseArrayCodec.class, "T");
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, T value, int uniqueId, long options) {
		char mark = '{';
		// write class name
		boolean className = CodecOptions.WriteClassName.isOptions(options);
		if (className) {
			buf.writeValue(mark, TYPE, value.getClass().getName());
			mark = ',';
		}
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		if (uniqueId >= 0 || className) {
			buf.writeName(mark, FIELD_VALUE);
		}
		buf.writeMark('[');
		encode0(context, buf, value, options);
		buf.writeMark(']');
		if (uniqueId >= 0 || className) {
			buf.writeMark('}');
		}
	}

	@Override
	public T decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.LBRACKET) {
			// 原生进入 [x, y, z]
			return decode0(context, buf, type, options);
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> clazz = typeCheck(context, buf, options);
		if (clazz != null) {
			if (clazz != this.clazz) { // 基础类型，必须直接兼容
				throw new IncompatibleTypeException(clazz, this.clazz);
			}
			buf.nextToken();
		}
		int uniqueId = -1;
		T value = null;
		for (; ; ) {
			// 断言当前位置一定是一个对象
			buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
			// 判断是否 "@id"
			if (token == SyntaxToken.STRING && buf.checkPutId()) {
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(numberExpects, stringExpects);
				uniqueId = buf.intValue();
			} else if (token == SyntaxToken.STRING && buf.checkGetValue()) {
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
				value = decode0(context, buf, type, options);
			} else if (false) {
				// TODO 新增宽松的解决方案
				buf.nextToken();
				buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS); // 预期当前语法是一个对象
				context.decode(buf, Object.class, false, options); // 读取一个对象
			} else {
				throw new UnknownSyntaxException("array error");
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.nextToken();
		}
		if (uniqueId != -1) {
			context.put(uniqueId, value);
		}
		return value;
	}

	protected abstract void encode0(AbstractContext context, AbstractBuffer buf, T values, long options);

	protected abstract T decode0(IDecodeContext context, ReaderBuffer buf, Type type, long options);
}