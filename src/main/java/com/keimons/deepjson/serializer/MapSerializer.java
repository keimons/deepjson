package com.keimons.deepjson.serializer;

import com.keimons.deepjson.DeepJsonConfig;
import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

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
		if (SerializerOptions.ForceClassName.isOptions(options)) {
			if (DeepJsonConfig.WHITE_MAP.contains(object.getClass())) {
				// 写入类型 /*@type:*/
				length += 10 + SerializerUtil.length(object.getClass().getName());
			}
		}
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
		buf.writeMark('{');
		// write class name
		if (SerializerOptions.ForceClassName.isOptions(options)) {
			Class<?> clazz = object.getClass();
			if (DeepJsonConfig.WHITE_COLLECTION.contains(clazz)) {
				buf.writeType(clazz);
			}
		}
		Class<?> keyCache = null;
		ISerializer keySerializer = null;
		Class<?> valueCache = null;
		ISerializer valueSerializer = null;
		int i = 0;
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			if (i != 0) {
				buf.writeMark(',');
			}
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
			i++;
		}
		buf.writeMark('}');
	}
}