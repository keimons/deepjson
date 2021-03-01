package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.util.SerializerUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

public class NormalNode$DeepJson implements ISerializer {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final byte[] node1$LATIN = {34, 110, 111, 100, 101, 49, 34, 58};
	private final byte[] node1$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 49, 0, 34, 0, 58, 0};

	private final byte[] node2$LATIN = {34, 110, 111, 100, 101, 50, 34, 58};
	private final byte[] node2$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 50, 0, 34, 0, 58, 0};

	private final byte[] node3$LATIN = {34, 110, 111, 100, 101, 51, 34, 58};
	private final byte[] node3$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 51, 0, 34, 0, 58, 0};

	private final byte[] node4$LATIN = {34, 110, 111, 100, 101, 52, 34, 58};
	private final byte[] node4$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 52, 0, 34, 0, 58, 0};

	private final byte[] node5$LATIN = {34, 110, 111, 100, 101, 53, 34, 58};
	private final byte[] node5$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 53, 0, 34, 0, 58, 0};

	private final byte[] node6$LATIN = {34, 110, 111, 100, 101, 54, 34, 58};
	private final byte[] node6$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 54, 0, 34, 0, 58, 0};

	private final byte[] node10$LATIN = {34, 110, 111, 100, 101, 49, 48, 34, 58};
	private final byte[] node10$UTF16 = {34, 0, 110, 0, 111, 0, 100, 0, 101, 0, 49, 0, 48, 0, 34, 0, 58, 0};

	private ISerializer node6$CODER;

	@Override
	public int length(Object object, long options) {
		if (object == null || SerializerOptions.IgnoreNonField.isOptions(options)) {
			return 4;
		}
		int length = 0;
		byte value0 = unsafe.getByte(object, 28L);
		length += SerializerUtil.size(value0) + 9;
		short value1 = unsafe.getShort(object, 24L);
		length += SerializerUtil.size(value1) + 9;
		int value2 = unsafe.getInt(object, 12L);
		length += SerializerUtil.size(value2) + 9;
		long value3 = unsafe.getLong(object, 16L);
		length += SerializerUtil.size(value3) + 9;
		boolean value4 = unsafe.getBoolean(object, 29L);
		length += (value4 ? 4 : 5) + 9;
		length += 11;
		Object value6 = unsafe.getObject(object, 32L);
		if (value6 == null) {
			if (!SerializerOptions.IgnoreNonField.isOptions(options)) {
				length += 14;
			}
		} else {
			ISerializer serializer = SerializerFactory.getSerializer(value6.getClass());
			length += serializer.length(value6, options) + 10;
		}
		if (length == 0) {
			length++;
		}
		length++;
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		if (object == null) {
			return 0;
		}
		char value5 = unsafe.getChar(object, 26L);
		if (value5 >>> 8 != 0) {
			return 1;
		}
		byte coder = 0;
		Object value6 = unsafe.getObject(object, 32L);
		if (value6 != null) {
			coder = SerializerFactory.getSerializer(value6.getClass()).coder(object, options);
			if (coder == 1) {
				return 1;
			}
		}
		return 0;
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
			byte value0 = unsafe.getByte(object, 28L);
			buf.writeValue(mark, node1$UTF16, value0);
			mark = ',';
			short value1 = unsafe.getShort(object, 24L);
			buf.writeValue(mark, node2$UTF16, value1);
			mark = ',';
			int value2 = unsafe.getInt(object, 12L);
			buf.writeValue(mark, node3$UTF16, value2);
			mark = ',';
			long value3 = unsafe.getLong(object, 16L);
			buf.writeValue(mark, node4$UTF16, value3);
			mark = ',';
			boolean value4 = unsafe.getBoolean(object, 29L);
			buf.writeValue(mark, node5$UTF16, value4);
			mark = ',';
			char value5 = unsafe.getChar(object, 26L);
			buf.writeValue(mark, node6$UTF16, value5);
			mark = ',';
			Object value6 = unsafe.getObject(object, 32L);
			buf.writeValue(mark, node10$UTF16, value6);
			mark = ',';
		}
		buf.writeEndObject();
	}
}