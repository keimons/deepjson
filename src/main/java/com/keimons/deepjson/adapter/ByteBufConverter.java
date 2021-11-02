package com.keimons.deepjson.adapter;

import com.keimons.deepjson.IConverter;
import io.netty.buffer.ByteBuf;

/**
 * {@link ByteBuf}转化器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ByteBufConverter implements IConverter<ByteBuf> {

	@Override
	public ByteBuf ensureWritable(ByteBuf dest, int writable) {
		dest.ensureWritable(writable + 6); // TLV结构，2 + 4 + length
		dest.writeShort(0); // dest.writeShort(type);
		dest.writeInt(writable); // dest.writeInt(length);
		return dest;
	}

	@Override
	public void writeByte(ByteBuf dest, int index, int value) {
		dest.writeByte(value);
	}
}