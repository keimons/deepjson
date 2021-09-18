package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.Config;

/**
 * 基础类型数组编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class BasePrimitiveArrayCodec<T> extends BaseCodec<T> {

	@Override
	public void encode(AbstractContext node, AbstractBuffer buf, T value, int uniqueId, long options) {
		char mark = '{';
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		// write class name
		if (CodecOptions.WriteClassName.isOptions(options)) {
			Class<?> clazz = value.getClass();
			if (Config.WHITE_COLLECTION.contains(clazz)) {
				buf.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0 || CodecOptions.WriteClassName.isOptions(options)) {
			buf.writeName(mark, FIELD_VALUE);
		}
		buf.writeMark('[');
		write0(value, buf, options);
		buf.writeMark(']');
		if (uniqueId >= 0) {
			buf.writeMark('}');
		}
	}

	protected abstract void write0(T values, AbstractBuffer buf, long options);
}