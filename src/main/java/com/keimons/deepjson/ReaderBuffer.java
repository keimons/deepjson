package com.keimons.deepjson;

import com.keimons.deepjson.support.SyntaxToken;

import java.io.Closeable;

/**
 * 读取缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class ReaderBuffer implements Closeable {

	/**
	 * 获取基础数据
	 *
	 * @return 基础数据
	 */
	public abstract char[] base();

	/**
	 * 获取读取位置
	 *
	 * @return 读取位置
	 */
	public abstract int readerIndex();

	/**
	 * 标记读取位置
	 *
	 * @see #resetReaderIndex() 重置读取位置
	 */
	public abstract void markReaderIndex();

	/**
	 * 重置读取位置
	 * <p>
	 * 重置读取位置之前必须先标记读取位置，否则将重置为初试位置。
	 * <pre>
	 *     int readerIndex = readerIndex();
	 *     markReaderIndex();
	 *     nextToken();
	 *     ...
	 *     resetReaderIndex();
	 *     assert readerIndex == readerIndex();
	 * </pre>
	 *
	 * @see #markReaderIndex() 标记读取位置
	 */
	public abstract void resetReaderIndex();

	/**
	 * 重置读取位置到指定位置
	 *
	 * <pre>
	 *     int readerIndex = readerIndex();
	 *     nextToken();
	 *     ...
	 *     resetReaderIndex(readerIndex);
	 *     assert readerIndex == readerIndex();
	 * </pre>
	 *
	 * @param readerIndex 新的读取位置
	 */
	public abstract void resetReaderIndex(int readerIndex);

	/**
	 * 查找下一个可用命令
	 *
	 * @return 下一个可用命令
	 */
	public abstract SyntaxToken token();

	/**
	 * 查找下一个可用命令
	 *
	 * @return 下一个可用命令
	 */
	public abstract SyntaxToken nextToken();

	/**
	 * 断言当前语法
	 * <p>
	 * 如果当前语法不是预期语法则抛出{@link CodecException}异常。
	 *
	 * @param expect 预期语法
	 * @throws CodecException 预期语法异常
	 */
	public abstract void assertExpectedSyntax(SyntaxToken expect) throws CodecException;

	/**
	 * 断言当前语法
	 * <p>
	 * 如果当前语法不是预期语法则抛出{@link CodecException}异常。
	 *
	 * @param expect1 预期语法
	 * @param expect2 预期语法
	 * @throws CodecException 预期语法异常
	 */
	public abstract void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2) throws CodecException;

	/**
	 * 断言当前语法
	 * <p>
	 * 如果当前语法不是预期语法则抛出{@link CodecException}异常。
	 *
	 * @param expect1 预期语法
	 * @param expect2 预期语法
	 * @param expect3 预期语法
	 * @throws CodecException 预期语法异常
	 */
	public abstract void assertExpectedSyntax(SyntaxToken expect1, SyntaxToken expect2, SyntaxToken expect3) throws CodecException;

	/**
	 * 断言当前语法
	 * <p>
	 * 如果当前语法不是预期语法则抛出{@link CodecException}异常。
	 *
	 * @param expects 预期语法
	 * @throws CodecException 预期语法异常
	 */
	public abstract void assertExpectedSyntax(SyntaxToken... expects) throws CodecException;

	/**
	 * 获取当前缓冲区的{@code hashcode}值
	 *
	 * @return 缓冲区内容的hashcode值
	 */
	public abstract int valueHashcode();

	/**
	 * 获取该token下的字符串值
	 *
	 * @return 字符串
	 */
	public abstract String stringValue();

	/**
	 * 自适应数字值
	 *
	 * @return 数字
	 */
	public abstract Number adaptiveNumber();

	/**
	 * 获取{@code boolean}值
	 *
	 * @return {@code boolean}值
	 */
	public abstract boolean booleanValue();

	/**
	 * 获取{@code byte}值
	 *
	 * @return {@code byte}值
	 */
	public abstract byte byteValue();

	/**
	 * 获取{@code short}值
	 *
	 * @return {@code short}值
	 */
	public abstract short shortValue();

	/**
	 * 获取{@code char}值
	 *
	 * @return {@code char}值
	 */
	public abstract char charValue();

	/**
	 * 获取{@code int}数字值
	 *
	 * @return 数字值
	 */
	public abstract int intValue();

	/**
	 * 获取{@code long}数字值
	 *
	 * @return 数字值
	 */
	public abstract long longValue();

	/**
	 * 获取{@code float}数字值
	 *
	 * @return 数字值
	 */
	public abstract float floatValue();

	/**
	 * 获取{@code double}数字值
	 *
	 * @return 数字值
	 */
	public abstract double doubleValue();

	/**
	 * 判断缓冲区和目标是否相同
	 *
	 * @param values 相同
	 * @return 是否相同
	 */
	public abstract boolean isSame(char[] values);

	/**
	 * 检测是否引用其他对象
	 *
	 * @return 是否引用其他对象
	 */
	public abstract boolean is$Id();

	/**
	 * 获取引用对象的ID
	 *
	 * @return 引用对象的ID
	 */
	public abstract int get$Id();

	/**
	 * 检测是否存放ID
	 *
	 * @return 是否存放ID键
	 */
	public abstract boolean checkPutId();

	/**
	 * 检测是否类型
	 *
	 * @return 是否类型键
	 */
	public abstract boolean checkGetType();

	/**
	 * 检测是否值
	 *
	 * @return 是否值
	 */
	public abstract boolean checkGetValue();

	@Override
	public abstract void close();

	@Override
	public int hashCode() {
		return valueHashcode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharArrayNode) {
			return isSame(((CharArrayNode) obj).values);
		}
		return super.equals(obj);
	}
}