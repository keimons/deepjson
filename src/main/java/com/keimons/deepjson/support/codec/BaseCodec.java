package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;

/**
 * 基础编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class BaseCodec<T> implements ICodec<T> {

	/**
	 * 循环引用中给一个对象标记一个唯一ID
	 * <p>
	 * {@link ReaderBuffer#checkPutId()}检测是否存放值
	 */
	public static final char[] FIELD_SET_ID = "@id".toCharArray();

	/**
	 * 循环引用中根据对象唯一ID获取对象
	 * <p>
	 * {@link ReaderBuffer#is$Id()} 是否引用对象
	 * {@link ReaderBuffer#get$Id()} 引用对象
	 */
	public static final char[] FIELD_GET_ID = "$id".toCharArray();

	/**
	 * 编码对象时存入对象的类型
	 * <p>
	 * {@link ReaderBuffer#checkGetType()}检测是否获取类型
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
	public CodecType getCodecType() {
		return CodecType.CODEC;
	}

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void build(AbstractContext context, T value) {

	}

	@Override
	public T decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		return null;
	}

	/**
	 * 类型检测
	 * <p>
	 * 从当前语法处检测，如果是{@link #TYPE}则表明这个json自带类型描述。
	 *
	 * @param context 上下文
	 * @param buf     缓冲区
	 * @param options 解码选项
	 * @return 类型
	 */
	protected Class<?> typeCheck(IDecodeContext context, ReaderBuffer buf, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.STRING && buf.checkGetType()) {
			buf.nextToken();
			buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
			buf.nextToken();
			buf.assertExpectedSyntax(stringExpects);
			String type = buf.stringValue();
			buf.nextToken(); // 读取下一个token
			try {
				return Class.forName(type);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	/**
	 * 判断一个key是否被引用
	 *
	 * @param instance 对象实例
	 * @param context  上下文
	 * @param buf      缓冲区
	 * @param options  解码选项
	 * @return 是否被引用
	 */
	protected boolean isInstanceId(Object instance, IDecodeContext context, ReaderBuffer buf, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.STRING && buf.checkPutId()) {
			buf.nextToken();
			buf.assertExpectedSyntax(colonExpects); // 预期当前语法是 ":"
			buf.nextToken();
			buf.assertExpectedSyntax(numberExpects, stringExpects);
			int uniqueId = buf.intValue();
			context.put(uniqueId, instance);
			return true;
		}
		return false;
	}
}