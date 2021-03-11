package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;

import java.util.Map;

/**
 * {@link Map}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class MapSerializer implements ISerializer {

	public static final MapSerializer instance = new MapSerializer();

	@Override
	public int length(Object object, long options) {
		int length = 2;
		Map<?, ?> values = (Map<?, ?>) object;
		Class<?> keyCache = null;
		Class<?> valueCache = null;
		ISerializer keySerializer = null;
		ISerializer valueSerializer = null;
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			{ // key
				Object key = entry.getKey();
				if (key == null) {
					length += 7;
				} else {
					if (key.getClass() != keyCache) {
						keyCache = key.getClass();
						keySerializer = SerializerFactory.getSerializer(keyCache);
					}
					length += keySerializer.length(key, options) + 1;
				}
			}
			{ // value
				Object value = entry.getValue();
				if (value == null) {
					length += 6;
				} else {
					if (value.getClass() != valueCache) {
						valueCache = value.getClass();
						valueSerializer = SerializerFactory.getSerializer(valueCache);
					}
					length += valueSerializer.length(value, options);
				}
			}
		}
		// 计算分隔符数量
		length += values.size() > 1 ? values.size() - 1 : 0;
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		Map<?, ?> values = (Map<?, ?>) object;
		Class<?> keyCache = null;
		ISerializer keySerializer = null;
		Class<?> valueCache = null;
		ISerializer valueSerializer = null;
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			{ // key
				Object key = entry.getKey();
				if (key != null) {
					if (key.getClass() != keyCache) {
						keyCache = key.getClass();
						keySerializer = SerializerFactory.getSerializer(keyCache);
					}
					if (keySerializer.coder(key, options) == 1) {
						return 1;
					}
				}
			}
			{ // value
				Object value = entry.getValue();
				if (value != null) {
					if (value.getClass() != valueCache) {
						valueCache = value.getClass();
						valueSerializer = SerializerFactory.getSerializer(valueCache);
					}
					if (valueSerializer.coder(value, options) == 1) {
						return 1;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		Map<?, ?> values = (Map<?, ?>) object;
		char mark = '{';
		Class<?> keyCache = null;
		ISerializer keySerializer = null;
		Class<?> valueCache = null;
		ISerializer valueSerializer = null;
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			buf.writeMark(mark);
			{ // key
				Object key = entry.getKey();
				if (key == null) {
					buf.writeNull();
				} else {
					if (key.getClass() != keyCache) {
						keyCache = key.getClass();
						keySerializer = SerializerFactory.getSerializer(keyCache);
					}
					keySerializer.write(key, options, buf);
				}
			}
			buf.writeMark(':');
			{ // value
				Object value = entry.getValue();
				if (value == null) {
					buf.writeNull();
				} else {
					if (value.getClass() != valueCache) {
						valueCache = value.getClass();
						valueSerializer = SerializerFactory.getSerializer(valueCache);
					}
					valueSerializer.write(value, options, buf);
				}
			}
			mark = ',';
		}
		if (mark == '{') {
			buf.writeMark(mark);
		}
		buf.writeMark('}');
	}
}