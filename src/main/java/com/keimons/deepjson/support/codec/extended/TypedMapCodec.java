package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;
import com.keimons.deepjson.support.codec.NullCodec;
import com.keimons.deepjson.util.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 已知类型的字典编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypedMapCodec<K, V> extends AbstractOnlineCodec<Map<K, V>> {

	final Class<K> kClass;

	ICodec<K> kCodec;

	final Class<V> vClass;

	ICodec<V> vCodec;

	@SuppressWarnings("unchecked")
	public TypedMapCodec(Field field) {
		Type type = field.getGenericType();
		ParameterizedType pt = (ParameterizedType) type;
		kClass = (Class<K>) ClassUtil.findGenericType(pt, Map.class, "K");
		vClass = (Class<V>) ClassUtil.findGenericType(pt, Map.class, "V");
		assert kClass != null;
		assert vClass != null;
		this.kCodec = CodecFactory.getCodec(kClass);
		this.vCodec = CodecFactory.getCodec(vClass);
	}

	public static boolean test(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (Map.class.isAssignableFrom((Class<?>) pt.getRawType())) {
				Type innerType1 = ClassUtil.findGenericType(pt, Map.class, "K");
				Type innerType2 = ClassUtil.findGenericType(pt, Map.class, "V");
				if (innerType1 != null || innerType2 != null) {
					if (CodecConfig.TYPED_CLASS.contains(innerType1)) {
						if (CodecConfig.TYPED_CLASS.contains(innerType2)) {
							return true;
						}
						if (innerType2 instanceof Class && Enum.class.isAssignableFrom((Class<?>) innerType2)) {
							return true;
						}
					}
					if (innerType1 instanceof Class && Enum.class.isAssignableFrom((Class<?>) innerType1)) {
						if (CodecConfig.TYPED_CLASS.contains(innerType2)) {
							return true;
						}
						if (innerType2 instanceof Class && Enum.class.isAssignableFrom((Class<?>) innerType2)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Map<K, V> map, int uniqueId, long options) throws IOException {
		char mark = '{';
		// write class name
		if (CodecOptions.WriteClassName.isOptions(options)) {
			Class<?> clazz = map.getClass();
			if (CodecConfig.WHITE_MAP.contains(clazz)) {
				writer.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0) {
			writer.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		for (Map.Entry<K, V> entry : map.entrySet()) {
			writer.writeMark(mark);
			K key = entry.getKey();
			if (key == null) {
				NullCodec.instance.encode(context, writer, CodecModel.K, null, -1, options);
			} else {
				kCodec.encode(context, writer, CodecModel.K, key, -1, options);
			}
			writer.writeMark(':');
			V value = entry.getValue();
			if (value == null) {
				NullCodec.instance.encode(context, writer, CodecModel.V, null, -1, options);
			} else {
				vCodec.encode(context, writer, CodecModel.V, value, -1, options);
			}
			mark = ',';
		}
		if (mark == '{') {
			writer.writeMark('{');
		}
		writer.writeMark('}');
	}

	@Override
	public Map<K, V> decode(final ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		throw new UnsupportedOperationException();
	}
}