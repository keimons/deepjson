package com.keimons.deepjson;

import com.keimons.deepjson.support.ReferenceNode;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.support.codec.MapCodec;
import com.keimons.deepjson.support.codec.ObjectCodec;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 编解码器
 * <p>
 * DeepJson内部采用的是节点的形式，将对象转化成一个个的节点，编解码的过程中，并不
 * 是在编码对象，而是在编码节点。
 * <p>
 * DeepJson在编解码的过程中，需要经过2次遍历所有对象：使用深度优先算法找到所有待
 * 编译对象，然后进行编译。DeepJson采用的是{@code "@id":unique}和
 * {@code "$id":unique}处理循环引用的问题。
 * <p>
 * 关于循环引用：
 * DeepJson没用采用位置定位，而是采用的给对象增加额外的属性，来实现标记和查找引用
 * 对象，这使得一些对象的编码结构将发生改变，例如：
 * {@code "[0,1,2,3, ... ]"}
 * 将会被编码为：
 * {@code "{"@id":unique,"@type":"class","value":[0,1,2,3, ... ]}"}
 * 同时，采用标记查找的方案，可以处理key的循环引用问题。json的编码是无序的，不能
 * 通过写入{@code index}来实现对key的查找，而DeepJson采用的id标记则可以处理这
 * 个问题，例如：
 * {@code {"@id":unique,"value":"v"}:{"$id":unique}}
 * <p>
 * 关于类型写入：
 * DeepJson允许{@link CodecOptions#WriteClassName}启用类型写入，但
 * 仅可写入{@link Config#WHITE_OBJECT}白名单中允许写入的类型。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface ICodec<T> {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	/**
	 * 是否需要建立循环索引
	 *
	 * @return {@code true}需要搜索，{@code false}不需要搜索
	 */
	boolean isSearch();

	/**
	 * 根据上下文信息，构造对象链。采用深度优先搜索，将所有的对象构成一个链条，等待写入缓冲区。
	 *
	 * @param context 上下文
	 * @param value   对象值
	 */
	void build(AbstractContext context, T value);

	/**
	 * 编码
	 *
	 * @param context  上下文
	 * @param buf      缓冲区
	 * @param value    对象值
	 * @param uniqueId 对象唯一ID
	 * @param options  编码选项
	 */
	void encode(AbstractContext context, AbstractBuffer buf, T value, int uniqueId, long options);

	/**
	 * 解码
	 * <p>
	 * 当前token有可能是{@link SyntaxToken#COMMA}或{@link SyntaxToken#OBJECTS}。当
	 * {@link ObjectCodec}自适应编解码器，发现这是一个对象时，先直接调用{@link MapCodec}编
	 * 解码器。如果json结构中包含类型描述，则跳转到类型描述的解码器进行解码。此过程中，造成当前
	 * token是{@link SyntaxToken#COMMA}。
	 *
	 * @param context 上下文环境
	 * @param buf     缓冲区
	 * @param type    对象类型
	 * @param options 解码选项
	 * @return 解码后的对象{@link Object}或者{@link ReferenceNode}。
	 * @see MapCodec {@link Map}对象解码。
	 */
	T decode(IDecodeContext context, ReaderBuffer buf, Type type, long options);
}