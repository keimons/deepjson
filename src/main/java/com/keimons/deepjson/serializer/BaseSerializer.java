package com.keimons.deepjson.serializer;

import com.keimons.deepjson.UnsafeUtil;
import sun.misc.Unsafe;

/**
 * 基础序列化实现
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public abstract class BaseSerializer implements ISerializer {

	protected static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static long valueOffset;
	private static long coderOffset;

	static {
		try {
			valueOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		try {
			coderOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("coder"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	protected String newString(byte[] buf, byte coder) {
		try {
			String str = (String) unsafe.allocateInstance(String.class);
			unsafe.putObject(str, valueOffset, buf);
			unsafe.putByte(str, coderOffset, coder);
			return str;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String write(Object object) {
		int size = length(object);
		byte coder = coder(object);
		byte[] value = new byte[size << coder];
		write(object, value, coder, 0);
		return newString(value, coder);
	}
}