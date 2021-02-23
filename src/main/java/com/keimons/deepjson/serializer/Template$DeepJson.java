package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.filler.SerializerUtil;
import sun.misc.Unsafe;

import java.text.DecimalFormat;

public class Template$DeepJson implements ISerializerWriter {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final byte[] value0$LATIN = {34, 118, 97, 108, 117, 101, 48, 34, 58};
	private final byte[] value0$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 48, 0, 34, 0, 58, 0};

	private final byte[] value1$LATIN = {34, 118, 97, 108, 117, 101, 49, 34, 58};
	private final byte[] value1$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 49, 0, 34, 0, 58, 0};

	private final byte[] value2$LATIN = {34, 118, 97, 108, 117, 101, 50, 34, 58};
	private final byte[] value2$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 50, 0, 34, 0, 58, 0};

	private final byte[] value4$LATIN = {34, 118, 97, 108, 117, 101, 52, 34, 58};
	private final byte[] value4$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 52, 0, 34, 0, 58, 0};

	private final byte[] value5$LATIN = {34, 118, 97, 108, 117, 101, 53, 34, 58};
	private final byte[] value5$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 53, 0, 34, 0, 58, 0};

	private final byte[] value6$LATIN = {34, 118, 97, 108, 117, 101, 54, 34, 58};
	private final byte[] value6$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 54, 0, 34, 0, 58, 0};

	private final byte[] value7$LATIN = {34, 118, 97, 108, 117, 101, 55, 34, 58};
	private final byte[] value7$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 55, 0, 34, 0, 58, 0};

	private final byte[] value8$LATIN = {34, 118, 97, 108, 117, 101, 56, 34, 58};
	private final byte[] value8$UTF16 = {34, 0, 118, 0, 97, 0, 108, 0, 117, 0, 101, 0, 56, 0, 34, 0, 58, 0};

	private final DecimalFormat format = new DecimalFormat();

	@Override
	public int length(Object object, long options) {
		if (object == null || SerializerOptions.IgnoreNonField.isOptions(options)) {
			return 4;
		}
		int length = 0;
		boolean value0 = unsafe.getBoolean(object, 40L);
		length += (value0 ? 4 : 5) + 10;
		byte value1 = unsafe.getByte(object, 41L);
		length += SerializerUtil.size(value1) + 10;
		length += 12;
		short value3 = unsafe.getShort(object, 38L);
		length += SerializerUtil.size(value3) + 10;
		int value4 = unsafe.getInt(object, 12L);
		length += SerializerUtil.size(value4) + 10;
		long value5 = unsafe.getLong(object, 16L);
		length += SerializerUtil.size(value5) + 10;
		float value6 = unsafe.getFloat(object, 32L);
		length += format.format(value6).length() + 10;
		double value7 = unsafe.getDouble(object, 24L);
		length += format.format(value7).length() + 10;
		if (length == 0) {
			length++;
		}
		length++;
		return length;
	}

	@Override
	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
			return;
		}
		if (buf.getCoder() == 0) {
		} else {
			byte mark = '{';
			boolean value0 = unsafe.getBoolean(object, 40L);
			buf.writeValue(mark, value0$UTF16, value0);
			mark = ',';
			byte value1 = unsafe.getByte(object, 41L);
			buf.writeValue(mark, value1$UTF16, value1);
			mark = ',';
			char value2 = unsafe.getChar(object, 36L);
			buf.writeValue(mark, value2$UTF16, value2);
			mark = ',';
			short value3 = unsafe.getShort(object, 38L);
			buf.writeValue(mark, value4$UTF16, value3);
			mark = ',';
			int value4 = unsafe.getInt(object, 12L);
			buf.writeValue(mark, value5$UTF16, value4);
			mark = ',';
			long value5 = unsafe.getLong(object, 16L);
			buf.writeValue(mark, value6$UTF16, value5);
			mark = ',';
			float value6 = unsafe.getFloat(object, 32L);
			buf.writeValue(mark, value7$UTF16, value6);
			mark = ',';
			double value7 = unsafe.getDouble(object, 24L);
			buf.writeValue(mark, value8$UTF16, value7);
			mark = ',';
		}
		buf.writeEndObject();
	}
}