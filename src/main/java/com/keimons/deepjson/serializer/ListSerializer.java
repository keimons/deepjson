package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

import java.util.List;

/**
 * {@link List}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ListSerializer implements ISerializer {

	public static final ListSerializer instance = new ListSerializer();

	@Override
	public int length(Object object, long options) {
		List<?> values = (List<?>) object;
		Class<?> cache = null;
		ISerializer serializer = null;
		int length = 2;
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
	public void write(Object object, ByteBuf buf) {
		List<?> values = (List<?>) object;
		byte mark = '[';
		Class<?> cache = null; // 缓存
		ISerializer serializer = null; // 缓存序列化工具
		for (Object value : values) {
			buf.writeMark((char) mark);
			if (value == null) {
				buf.writeNull();
			} else {
				if (value.getClass() != cache) {
					cache = value.getClass();
					serializer = SerializerFactory.getSerializer(cache);
				}
				serializer.write(value, buf);
			}
			mark = ',';
		}
		if (values.size() <= 0) {
			buf.writeMark('[');
		}
		buf.writeMark(']');
	}
}