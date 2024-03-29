package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.lang.reflect.Type;

/**
 * {@link Class}类型编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class KlassCodec<T> implements ICodec<T> {

	/**
	 * 循环引用中给一个对象标记一个唯一ID
	 * <p>
	 * {@link JsonReader#checkPutId()}检测是否存放值
	 */
	public static final char[] FIELD_SET_ID = "@id".toCharArray();

	/**
	 * 循环引用中根据对象唯一ID获取对象
	 * <p>
	 * {@link JsonReader#get$Id()} 引用对象
	 */
	public static final char[] FIELD_GET_ID = "$id".toCharArray();

	/**
	 * 编码对象时存入对象的类型
	 * <p>
	 * {@link JsonReader#checkGetType()}检测是否获取类型
	 */
	public static final char[] TYPE = "$type".toCharArray();

	/**
	 * 对象需要包装时，将对象存入这个字段中
	 * <p>
	 * 例如，数组的编码{@code [0,1,2,3]}将会被编码为{@code {"@id":0,"$value":[0,1,2,3]}}。
	 */
	public static final char[] FIELD_VALUE = "$value".toCharArray();

	/**
	 * 数字预期语法
	 */
	protected final SyntaxToken numberExpects = SyntaxToken.NUMBER;

	/**
	 * 字符串预期语法
	 */
	protected final SyntaxToken stringExpects = SyntaxToken.STRING;

	/**
	 * 冒号预期语法
	 */
	protected final SyntaxToken colonExpects = SyntaxToken.COLON;

	@Override
	public void build(WriterContext context, T value) {

	}

	@Override
	public final T decode(ReaderContext context, JsonReader reader, Type type, long options) {
		return decode(context, reader, (Class<?>) type, options);
	}

	/**
	 * 解码
	 *
	 * @param context 上下文环境
	 * @param reader  读取器
	 * @param clazz   对象类型
	 * @param options 解码选项
	 * @return 解码后的对象
	 */
	protected abstract T decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options);

	/**
	 * 类型检测
	 * <p>
	 * 从当前语法处检测，如果是{@link #TYPE}则表明这个json自带类型描述。
	 *
	 * @param context 上下文
	 * @param reader  读取器
	 * @param options 解码选项
	 * @return 类型
	 */
	protected Class<?> typeCheck(ReaderContext context, JsonReader reader, long options) {
		SyntaxToken token = reader.token();
		if (token == SyntaxToken.STRING && reader.checkGetType()) {
			reader.nextToken();
			reader.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
			reader.nextToken();
			reader.assertExpectedSyntax(stringExpects);
			String type = reader.stringValue();
			reader.nextToken(); // 读取下一个token
			try {
				return Class.forName(type);
			} catch (ClassNotFoundException e) {
				throw new TypeNotFoundException(e);
			}
		}
		return null;
	}

	/**
	 * 判断一个key是否被引用
	 *
	 * @param instance 对象实例
	 * @param context  上下文
	 * @param reader   读取器
	 * @param options  解码选项
	 * @return 是否被引用
	 */
	protected boolean isInstanceId(Object instance, ReaderContext context, JsonReader reader, long options) {
		SyntaxToken token = reader.token();
		if (token == SyntaxToken.STRING && reader.checkPutId()) {
			reader.nextToken();
			reader.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
			reader.nextToken();
			reader.assertExpectedSyntax(numberExpects, stringExpects);
			int uniqueId = reader.intValue();
			context.put(uniqueId, instance);
			return true;
		}
		return false;
	}
}