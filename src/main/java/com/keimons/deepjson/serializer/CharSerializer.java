package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.SerializerUtil;

/**
 * char类型序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class CharSerializer implements ISerializer {

	public static final CharSerializer instance = new CharSerializer();

	@Override
	public int length(Object object, long options) {
		return 3;
	}

	@Override
	public byte coder(Object object, long options) {
		return ((char) object) >>> 8 == 0 ? SerializerUtil.LATIN : SerializerUtil.UTF16;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeChar((char) object);
		}
	}
}