package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

import java.util.*;

/**
 * {@link List}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ListSerializer implements ISerializer {

	public static final ListSerializer instance = new ListSerializer();

	private static final Set<Class<?>> WHITE_LIST = new HashSet<>();

	static {
		WHITE_LIST.add(ArrayList.class);
		WHITE_LIST.add(LinkedList.class);
	}

	@Override
	public int length(Object object, long options) {
		List<?> values = (List<?>) object;
		Class<?> cache = null;
		ISerializer serializer = null;
		int length = 2;
		if (SerializerOptions.ForceTypeNotes.isOptions(options)) {
			if (WHITE_LIST.contains(object.getClass())) {
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
		List<?> values = (List<?>) object;
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
		List<?> values = (List<?>) object;
		buf.writeMark('[');
		Class<?> cache = null; // 缓存
		ISerializer serializer = null; // 缓存序列化工具

		// 以注释的形式写入类名
		if (SerializerOptions.ForceTypeNotes.isOptions(options)) {
			Class<?> clazz = object.getClass();
			if (WHITE_LIST.contains(clazz)) {
				buf.writeType(clazz);
			}
		}

		for (int i = 0; i < values.size(); i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			Object value = values.get(i);
			if (value == null) {
				buf.writeNull();
			} else {
				if (value.getClass() != cache) {
					cache = value.getClass();
					serializer = SerializerFactory.getSerializer(cache);
				}
				serializer.write(value, options, buf);
			}
		}
		buf.writeMark(']');
	}
}