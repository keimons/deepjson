package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.Config;
import com.keimons.deepjson.support.ElementsFuture;

import java.util.Collection;

/**
 * {@link Collection}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CollectionCodec extends BaseCodec<Collection<?>> {

	public static final CollectionCodec instance = new CollectionCodec();

	@Override
	public void build(AbstractContext context, Collection<?> value) {
		ElementsFuture future = new ElementsFuture();
		context.cache(future, EmptyCodec.instance);
		for (Object obj : value) {
			context.build(obj);
			future.addCount();
		}
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Collection<?> value, int uniqueId, long options) {
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		int count = ((ElementsFuture) future).getCount();
		char mark = '{';
		if (uniqueId >= 0) {
			buf.writeValue(mark, FIELD_SET_ID, uniqueId);
			mark = ',';
		}
		// write class name
		boolean className = CodecOptions.WriteClassName.isOptions(options);
		if (className) {
			Class<?> clazz = value.getClass();
			if (Config.WHITE_COLLECTION.contains(clazz)) {
				buf.writeValue(mark, TYPE, clazz.getName());
				mark = ',';
			}
		}
		if (uniqueId >= 0 || className) {
			buf.writeName(mark, FIELD_VALUE);
		}
		buf.writeMark('[');
		for (int i = 0; i < count; i++) {
			if (i != 0) {
				buf.writeMark(',');
			}
			context.encode(buf, options);
		}
		buf.writeMark(']');
		if (uniqueId >= 0 || className) {
			buf.writeMark('}');
		}
	}
}