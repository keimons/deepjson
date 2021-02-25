package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import com.keimons.deepjson.filler.SerializerUtil;
import com.keimons.deepjson.filler.IFieldName;
import sun.misc.Unsafe;

/**
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
class WriterLatin implements IWriter<byte[]> {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	@Override
	public int writeStartObject(byte[] buf, long options, int writeIndex) {
		buf[writeIndex] = '{';
		return 1;
	}

	@Override
	public int writeEndObject(byte[] buf, long options, int writeIndex) {
		buf[writeIndex] = '}';
		return 1;
	}

	@Override
	public int writeStartArray(byte[] buf, long options, int writeIndex) {
		buf[writeIndex] = '[';
		return 1;
	}

	@Override
	public int writeEndArray(byte[] buf, long options, int writeIndex) {
		buf[writeIndex] = ']';
		return 1;
	}

	@Override
	public int writeMark(byte[] buf, long options, int writeIndex) {
		buf[writeIndex] = ',';
		return 1;
	}

	@Override
	public int writeNull(byte[] buf, long options, int writeIndex) {
		buf[writeIndex++] = 'n';
		buf[writeIndex++] = 'u';
		buf[writeIndex++] = 'l';
		buf[writeIndex++] = 'l';
		buf[writeIndex] = ',';
		return 5;
	}

	@Override
	public int writeField(byte[] buf, long options, int writeIndex, IFieldName filler) {
		for (byte b : filler.getFieldNameByLatin()) {
			buf[writeIndex++] = b;
		}
		return filler.length() - 1;
	}

	@Override
	public int writeBoolean(byte[] buf, long options, int writeIndex, boolean value) {
		if (value) {
			buf[writeIndex++] = 't';
			buf[writeIndex++] = 'r';
			buf[writeIndex++] = 'u';
			buf[writeIndex++] = 'e';
		} else {
			buf[writeIndex++] = 'f';
			buf[writeIndex++] = 'a';
			buf[writeIndex++] = 'l';
			buf[writeIndex++] = 's';
			buf[writeIndex++] = 'e';
		}
		buf[writeIndex] = ',';
		return value ? 5 : 6;
	}

	@Override
	public int writeChar(byte[] buf, long options, int writeIndex, char value) {
		buf[writeIndex++] = '"';
		buf[writeIndex++] = (byte) (value & 0xFF);
		buf[writeIndex++] = '"';
		buf[writeIndex] = ',';
		return 4;
	}

	@Override
	public int writeInt(byte[] buf, long options, int writeIndex, int value) {
		SerializerUtil.putLATIN(buf, writeIndex, value);
		buf[writeIndex] = ',';
		return 1;
	}

	@Override
	public int writeLong(byte[] buf, long options, int writeIndex, long value) {
		SerializerUtil.putLATIN(buf, writeIndex, value);
		buf[writeIndex] = ',';
		return 1;
	}

	@Override
	public int writeString(byte[] buf, long options, int writeIndex, String value) {
		assert unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING) == 0 : "error call for compact strings";
		int length = value.length();
		byte[] bytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		for (byte b : bytes) {
			buf[writeIndex++] = b;
		}
		buf[writeIndex] = ',';
		return length + 1;
	}

	@Override
	public int writeStringWithMark(byte[] buf, long options, int writeIndex, String value) {
		assert unsafe.getByte(value, SerializerUtil.CODER_OFFSET_STRING) == 0 : "error call for compact strings";
		buf[writeIndex++] = '"';
		int length = value.length();
		byte[] bytes = (byte[]) unsafe.getObject(value, SerializerUtil.VALUE_OFFSET_STRING);
		for (byte b : bytes) {
			buf[writeIndex++] = b;
		}
		buf[writeIndex++] = '"';
		buf[writeIndex] = ',';
		return length + 1;
	}

	@Override
	public int writeInts(byte[] buf, long options, int writeIndex, int[] values) {
		return 0;
	}

	@Override
	public int writeInts(byte[] buf, long options, int writeIndex, Integer[] values) {
		return 0;
	}
}