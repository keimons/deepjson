package com.keimons.deepjson;

import com.keimons.deepjson.support.context.DepthSearchContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 上下文信息
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractContext {

	public static AbstractContext defaultContext() {
		return new DepthSearchContext();
	}

	/**
	 * 缓存一个对象和它的编解码工具。
	 *
	 * @param value 需要缓存的对象
	 * @param codec 该对象对应的编解码工具
	 * @return {@code true}对象不存在，{@code false}对象已存在。
	 */
	public abstract <T> boolean cache(@Nullable T value, @NotNull ICodec<T> codec);

	/**
	 * 以对象为根节点，构建对象树。
	 *
	 * @param root 根节点
	 */
	public abstract void build(Object root);

	/**
	 * 检索并移除此上下文的头部对象，如果此队列为空，则返回{@code null}。
	 *
	 * @return 此上下文的头部，如果此队列为空，则为{@code null}。
	 */
	public abstract Object poll();

	/**
	 * 将对象写入指定缓冲区中
	 *
	 * @param buf 缓冲区
	 */
	public abstract void encodeKey(AbstractBuffer buf, long options);

	/**
	 * 将对象写入指定缓冲区中
	 *
	 * @param buf 缓冲区
	 */
	public abstract void encode(AbstractBuffer buf, long options);

	/**
	 * （作为值）将对象写入缓冲区中
	 *
	 * @param mark 分隔标识
	 * @param name 字段名称
	 * @return {@code true}已写入，{@code false}未写入。
	 */
	public abstract boolean encodeValue(AbstractBuffer buf, long options, char mark, char[] name);

	/**
	 * 释放上下文
	 */
	public abstract void release(AbstractBuffer buf);
}