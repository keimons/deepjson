package com.keimons.deepjson.serializer;

import com.keimons.deepjson.buffer.ByteBuf;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.SerializerUtil;

/**
 * {@link String}序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class StringSerializer implements ISerializer {

	public static final StringSerializer instance = new StringSerializer();

	@Override
	public int length(Object object, long options) {
		return SerializerUtil.length((String) object) + 2;
	}

	@Override
	public byte coder(Object object, long options) {
		if (PlatformUtil.javaVersion() >= 9) {
			return SerializerUtil.coder((String) object);
		} else {
			return 1;
		}
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		String value = (String) object;
		buf.writeString(value);
	}
}