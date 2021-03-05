package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.RyuFloat;

/**
 * float类型序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class FloatSerializer implements ISerializer {

	public static final FloatSerializer instance = new FloatSerializer();

	@Override
	public int length(Object object, long options) {
		return RyuFloat.length((float) object);
	}

	@Override
	public byte coder(Object object, long options) {
		return 0;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
		} else {
			buf.writeFloat((float) object);
		}
	}
}