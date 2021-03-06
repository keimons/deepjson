package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@link Character}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class CharSerializer implements ISerializer {

	public static final CharSerializer instance = new CharSerializer();

	@Override
	public int length(Object object, long options) {
		return SerializerUtil.length((char) object) + 2;
	}

	@Override
	public byte coder(Object object, long options) {
		return SerializerUtil.coder((char) object) == 0 ? SerializerUtil.LATIN : SerializerUtil.UTF16;
	}

	@Override
	public void write(Object object, long options, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeChar((char) object);
		}
	}
}