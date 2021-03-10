package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * 序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public interface ISerializer {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	/**
	 * 计算序列化时所需缓冲区的大小
	 *
	 * @param object  序列化对象
	 * @param options 序列化选项
	 * @return 缓冲区大小
	 */
	int length(Object object, long options);

	/**
	 * 计算序列化时的编码格式
	 * <p>
	 * JDK9+中，字符串存储由{@code char[]}修改为{@code byte[]}，对于单个字节可以编码的字符串
	 * 采用{@code 0}压缩字节，对于双字节编码的字符串采用{@code 1}膨胀字节。
	 *
	 * @param object  序列化对象
	 * @param options 序列化选项
	 * @return 编码格式
	 * @since 9
	 */
	byte coder(Object object, long options);

	/**
	 * 序列化对象至缓冲区
	 *
	 * @param object 序列化对象
	 * @param buf    缓冲区
	 */
	void write(Object object, ByteBuf buf);
}