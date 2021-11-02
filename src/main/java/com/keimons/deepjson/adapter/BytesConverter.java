package com.keimons.deepjson.adapter;

import com.keimons.deepjson.IConverter;

/**
 * 字节数组转化器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BytesConverter implements IConverter<byte[]> {

	@Override
	public byte[] ensureWritable(byte[] dest, int writable) {
		return new byte[writable];
	}

	@Override
	public void writeByte(byte[] dest, int index, int value) {
		dest[index] = (byte) value;
	}
}