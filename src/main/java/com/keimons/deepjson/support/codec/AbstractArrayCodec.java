package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.UnknownSyntaxException;
import com.keimons.deepjson.util.ClassUtil;

import java.io.IOException;
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
public abstract class AbstractArrayCodec<T> extends KlassCodec<T> {

	private final Class<?> clazz;

	public AbstractArrayCodec() {
		clazz = (Class<?>) ClassUtil.findGenericType(this.getClass(), AbstractArrayCodec.class, "T");
	}

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public boolean isCacheType() {
		return false;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, T value, int uniqueId, long options) throws IOException {
		char mark = '{';
		// write class name
		boolean className = CodecOptions.WriteClassName.isOptions(options);
		if (className) {
			writer.writeValue(mark, TYPE, value.getClass().getName());
			mark = ',';
		}
		if (uniqueId >= 0) {
			writer.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		if (uniqueId >= 0 || className) {
			writer.writeName(mark, FIELD_VALUE);
		}
		writer.writeMark('[');
		encode0(context, writer, value, options);
		writer.writeMark(']');
		if (uniqueId >= 0 || className) {
			writer.writeMark('}');
		}
	}

	@Override
	public T decode(ReaderContext context, ReaderBuffer buf, Class<?> type, long options) {
		Type componentType = type.getComponentType();
		Class<?> instanceType = type.getComponentType();
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.LBRACKET) {
			// 原生进入 [x, y, z]
			return decode0(context, buf, instanceType, componentType, options);
		}
		// 拓展进入 {"$type":"[X", "$values":[x, y, z]}
		token = buf.nextToken(); // 下一个有可能是对象也有可能是对象结束
		Class<?> clazz = typeCheck(context, buf, options);
		if (clazz != null) {
			if (!this.clazz.isAssignableFrom(clazz)) { // 必须是 对象数组类型 或 子类
				throw new IncompatibleTypeException(clazz, Object[].class);
			}
			// TODO 安全性检查
			instanceType = clazz.getComponentType();
			componentType = clazz.getComponentType();
			token = buf.nextToken();
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
				value = decode0(context, buf, instanceType, componentType, options);
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

	protected abstract void encode0(WriterContext context, JsonWriter writer, T values, long options) throws IOException;

	/**
	 * 解码数组
	 *
	 * @param context       上下文环境
	 * @param buf           缓冲区
	 * @param instanceType  实例类型，用于反射创建对象
	 * @param componentType 组件类型
	 * @param options       解码选项
	 * @return 数组对象
	 */
	protected abstract T decode0(ReaderContext context, ReaderBuffer buf, Class<?> instanceType, Type componentType, long options);
}