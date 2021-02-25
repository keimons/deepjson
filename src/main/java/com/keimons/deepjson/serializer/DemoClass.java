package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

public class DemoClass {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private final byte[] field1NameByUtf16 = {1, 3, 5};

	public void write(Object object, ByteBuf buf) {
		if (object == null) {
			buf.writeNull();
			return;
		}
		if (buf.getCoder() == 0) {
		} else {
			byte mark = '{';
			int value0 = unsafe.getInt(object, 50L);
			buf.writeValue(mark, field1NameByUtf16, value0);
			mark = ',';
		}
	}
}