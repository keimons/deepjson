package com.keimons.deepjson.support.context;

import com.keimons.deepjson.CodecModel;
import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.WriterContext;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.ReferenceNode;
import com.keimons.deepjson.support.codec.NullCodec;
import com.keimons.deepjson.support.codec.ReferenceCodec;
import com.keimons.deepjson.util.ArrayUtil;
import com.keimons.deepjson.util.WeakIdentityHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;

/**
 * 使用深度优先算法构建的用于存储节点上下文信息
 * <p>
 * 开始编码对象时，直接缓存对象中所有的节点，以防止对象在编码过程中发生引用改变。本类存在
 * 的意义便是解决对象在编码过程中的引用变化和循环引用的问题。
 * <p>
 * 使用地址索引，并不能解决key循环引用和重复key的问题。所以，DeepJson采用的id索引解决此问
 * 题。写入索引{@code "@id":unique}和引用索引{@code "$id:unique"}配合使用，可以定位
 * 到对象的位置。使用索引同样会带来新的问题，既：数组对象无法被准确的索引到。针对
 * {@code [x,y,z...]}当且仅当发生了循环引用时，将其编码为
 * {@code {"@id":unique,"value:":[x,y,z...]}}结构。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class DepthSearchContext extends WriterContext {

	/* 默认最大缓存 */
	private static final int MAXIMUM_CAPACITY = 64 * 1024; // 64k

	/* 默认初始缓存 */
	private static final int DEFAULT_CAPACITY = 64;

	/* 默认初始唯一标识 */
	private static final int DEFAULT_UNIQUE = -1;

	/**
	 * 存储所有节点信息
	 * <p>
	 * key: 对象, value: 节点信息
	 */
	private WeakIdentityHashMap<Object> context = new WeakIdentityHashMap<Object>(DEFAULT_UNIQUE);

	/**
	 * 深度优先算法构建的对象序列（有序）
	 * <p>
	 * 编码完成后，需要手动清空，如果没有手动清空，会造成内存泄漏。需要在
	 * {@link DepthSearchContext#release(JsonWriter)}}中手动清空的。
	 */
	private Object[] values = new Object[DEFAULT_CAPACITY];

	/**
	 * {@link #values}对应的唯一标识
	 */
	private int[] uniques = new int[DEFAULT_CAPACITY];

	/**
	 * {@link #values}对应的编解码工具
	 * <p>
	 * 不需要手动清空，所有的编解码工具都是单例模式，新的编解码工具写入时会直接覆盖旧对象。对于
	 * 空对象，也需要写入一个{@link NullCodec}编解码工具。
	 */
	private ICodec<Object>[] codecs = ArrayUtil.newInstance(ICodec.class, DEFAULT_CAPACITY);

	/**
	 * 写入位置
	 */
	private int writerIndex;

	/**
	 * 读取位置
	 */
	private int readerIndex;

	/**
	 * 自增的唯一标识
	 * <p>
	 * 取值范围{@code [1,∞)}，{@code 0}为特殊值，不能作为引用索引。写入索引
	 * {@code "@id":unique}和引用索引{@code "$id:unique"}。
	 */
	private int unique;

	@Override
	public final Object poll() {
		return values[readerIndex++];
	}

	@Override
	public void build(Object root) {
		ICodec<Object> codec = CodecFactory.getCodec(root);
		if (!cache(root, codec)) {
			return;
		}
		codec.build(this, root);
	}

	@Override
	public void build(Object root, ICodec<Object> codec) {
		if (!cache(root, codec)) {
			return;
		}
		codec.build(this, root);
	}

	@Override
	public boolean isEmptyHead() {
		return values[readerIndex] == null;
	}

	@Override
	public void encode(JsonWriter writer, CodecModel model, long options) throws IOException {
		Object value = values[readerIndex];
		int uniqueId = uniques[readerIndex];
		ICodec<Object> codec = codecs[readerIndex++];
		codec.encode(this, writer, model, value, uniqueId, options);
	}

	@Override
	public <T> boolean cache(@Nullable T value, @NotNull ICodec<T> codec) {
		int index;
		if (!codec.isSearch() || (index = context.putIfAbsent(value, writerIndex)) == DEFAULT_UNIQUE) {
			cache0(value, codec);
			return true;
		}
		int uniqueId = uniques[index];
		if (uniqueId == DEFAULT_UNIQUE) {
			uniqueId = ++unique;
			uniques[index] = uniqueId;
		}
		cache0(new ReferenceNode(uniqueId), ReferenceCodec.instance);
		return false;
	}

	/**
	 * 缓存一个对象
	 * <p>
	 * 对于{@code null}的节点，同样需要存储它的节点信息。
	 *
	 * @param value 缓存的对象，对于{@code null}同样需要缓存。
	 * @param codec 编解码工具
	 */
	@SuppressWarnings("unchecked")
	private <T> void cache0(@Nullable T value, @NotNull ICodec<T> codec) {
		if (writerIndex >= values.length) {
			values = Arrays.copyOf(values, values.length << 1);
			uniques = Arrays.copyOf(uniques, uniques.length << 1);
			codecs = Arrays.copyOf(codecs, codecs.length << 1);
		}
		values[writerIndex] = value;
		uniques[writerIndex] = DEFAULT_UNIQUE;
		codecs[writerIndex++] = (ICodec<Object>) codec;
	}

	@Override
	public void release(JsonWriter writer) throws IOException {
		if (context.capacity() > MAXIMUM_CAPACITY) {
			context = new WeakIdentityHashMap<Object>(DEFAULT_UNIQUE, MAXIMUM_CAPACITY);
		} else {
			context.clear();
		}
		if (writerIndex > MAXIMUM_CAPACITY) {
			values = new Object[MAXIMUM_CAPACITY];
			uniques = new int[MAXIMUM_CAPACITY];
			codecs = ArrayUtil.newInstance(ICodec.class, MAXIMUM_CAPACITY);
		} else {
			for (int i = 0; i < writerIndex; i++) {
				values[i] = null;
			}
		}
		this.unique = 0;
		this.readerIndex = 0;
		this.writerIndex = 0;
		writer.close();
	}
}