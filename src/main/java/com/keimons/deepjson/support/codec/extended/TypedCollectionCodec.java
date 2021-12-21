package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.CodecFactory;
import com.keimons.deepjson.support.codec.CollectionCodec;
import com.keimons.deepjson.support.codec.NullCodec;
import com.keimons.deepjson.util.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 已知类型的集合编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TypedCollectionCodec<T> extends CollectionCodec {

	final Class<T> clazz;

	ICodec<T> codec;

	@SuppressWarnings("unchecked")
	public TypedCollectionCodec(Field field) {
		Class<?> type = getInnerType(field);
		assert type != null;
		this.clazz = (Class<T>) type;
		this.codec = CodecFactory.getCodec(clazz);
	}

	public static boolean test(Field field) {
		return getInnerType(field) != null;
	}

	@Override
	public void build(WriterContext context, Collection<?> value) {

	}

	@Override
	@SuppressWarnings("unchecked")
	protected char encode(WriterContext context, JsonWriter writer, Collection<?> values, long options, char mark) throws IOException {
		for (Object value : values) {
			writer.writeMark(mark);
			if (value == null) {
				NullCodec.instance.encode(context, writer, CodecModel.V, null, -1, options);
			} else {
				codec.encode(context, writer, CodecModel.V, (T) value, -1, options);
			}
			mark = ',';
		}
		return mark;
	}

	private static Class<?> getInnerType(Field field) {
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			if (Collection.class.isAssignableFrom((Class<?>) pt.getRawType())) {
				Type innerType = ClassUtil.findGenericType(pt, Collection.class, "E");
				if (innerType != null) {
					if (CodecConfig.TYPED_CLASS.contains(innerType)) {
						return (Class<?>) innerType;
					}
					if (innerType instanceof Class && Enum.class.isAssignableFrom((Class<?>) innerType)) {
						return Enum.class;
					}
				}
			}
		}
		return null;
	}
}