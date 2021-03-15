package com.keimons.deepjson.serializer;

import com.keimons.deepjson.DeepJsonConfig;
import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

import java.util.Collection;

/**
 * {@link Collection}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class CollectionSerializer implements ISerializer {

	public static final CollectionSerializer instance = new CollectionSerializer();

	@Override
	public int length(Object object, long options) {
		Collection<?> values = (Collection<?>) object;
		Class<?> cache = null;
		ISerializer serializer = null;
		int length = 2;
		if (SerializerOptions.ForceClassName.isOptions(options)) {
			if (DeepJsonConfig.WHITE_COLLECTION.contains(object.getClass())) {
				// 写入类型 /*@type:*/
				// 例如："[/*@type:java.util.LinkedList*/]"、"[/*@type:java.util.concurrent.ConcurrentHashMap*/]"
				length += 10 + SerializerUtil.length(object.getClass().getName());
			}
		}
		for (Object value : values) {
			if (value == null) {
				length += 4;
				continue;
			}
			if (value.getClass() != cache) {
				cache = value.getClass();
				serializer = SerializerFactory.getSerializer(cache);
			}
			length += serializer.length(value, options);
		}
		// 计算分隔符数量
		length += values.size() > 1 ? values.size() - 1 : 0;
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		Collection<?> values = (Collection<?>) object;
		Class<?> cache = null;
		ISerializer serializer = null;
		for (Object value : values) {
			if (value == null) {
				continue;
			}
			if (value.getClass() != cache) {
				cache = value.getClass();
				serializer = SerializerFactory.getSerializer(value.getClass());
			}
			if (serializer.coder(value, options) == 1) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		Collection<?> values = (Collection<?>) object;
		buf.writeMark('[');
		Class<?> cache = null; // 缓存
		ISerializer serializer = null; // 缓存序列化工具

		// write class name
		if (SerializerOptions.ForceClassName.isOptions(options)) {
			Class<?> clazz = object.getClass();
			if (DeepJsonConfig.WHITE_COLLECTION.contains(clazz)) {
				buf.writeType(clazz);
			}
		}
		int i = 0;
		for (Object value : values) {
			if (i != 0) {
				buf.writeMark(',');
			}
			if (value == null) {
				buf.writeNull();
			} else {
				if (value.getClass() != cache) {
					cache = value.getClass();
					serializer = SerializerFactory.getSerializer(cache);
				}
				serializer.write(value, options, buf);
			}
			i++;
		}
		buf.writeMark(']');
	}
}