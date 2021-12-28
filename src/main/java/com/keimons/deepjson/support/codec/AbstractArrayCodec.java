package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
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
		writer.writeMark('[');
		encode0(context, writer, value, options);
		writer.writeMark(']');
	}

	@Override
	public T decode(ReaderContext context, JsonReader reader, Class<?> type, long options) {
		Type componentType = type.getComponentType();
		Class<?> instanceType = type.getComponentType();
		reader.assertExpectedSyntax(SyntaxToken.LBRACKET);
		return decode0(context, reader, instanceType, componentType, options);
	}

	protected abstract void encode0(WriterContext context, JsonWriter writer, T values, long options) throws IOException;

	/**
	 * 解码数组
	 *
	 * @param context       上下文环境
	 * @param reader        读取器
	 * @param instanceType  实例类型，用于反射创建对象
	 * @param componentType 组件类型
	 * @param options       解码选项
	 * @return 数组对象
	 */
	protected abstract T decode0(ReaderContext context, JsonReader reader, Class<?> instanceType, Type componentType, long options);
}