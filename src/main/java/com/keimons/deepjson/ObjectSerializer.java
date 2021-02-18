package com.keimons.deepjson;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;

public class ObjectSerializer implements ISerializer {

	protected static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private List<IFiller> fillers = new ArrayList<>();

	private static MethodHandle constructor;

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

	public int size(Object object) {
		int size = 0;
		for (IFiller filler : fillers) {
			size += filler.length(object);
		}
		return size;
	}

	@Override
	public byte coder(Object object) {
		for (IFiller filler : fillers) {
			byte coder = filler.coder(object);
			if (coder == 1) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public String concat(Object object) {
		int size = 2 + size(object);
		byte coder = coder(object);
		byte[] value = new byte[size << coder];
		concat(object, value, coder, 0);
		try {
			String str = (String) unsafe.allocateInstance(String.class);
			unsafe.putObject(str, valueOffset, value);
			unsafe.putByte(str, coderOffset, coder);
			return str;
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
		return null;
	}

	@Override
	public int concat(Object object, byte[] value, byte coder, int writeIndex) {
		int index = writeIndex << coder;
		if (coder == DeepHelper.LATIN) {
			value[index] = '{';
		} else {
			value[index] = UTF16_L[0];
			value[++index] = UTF16_L[1];
		}
		writeIndex++;
		for (IFiller filler : fillers) {
			writeIndex += filler.concat(object, value, coder, writeIndex);
		}
		index = writeIndex - 1 << coder;
		if (coder == DeepHelper.LATIN) {
			value[index] = '}';
		} else {
			value[index] = UTF16_R[0];
			value[++index] = UTF16_R[1];
		}
		writeIndex++;
		return writeIndex;
	}

	public void addLast(IFiller filler) {
		this.fillers.add(filler);
	}

	public void removeLast() {
		this.fillers.remove(fillers.size() - 1);
	}

	public List<IFiller> getFillers() {
		return fillers;
	}

	public void setFillers(List<IFiller> fillers) {
		this.fillers = fillers;
	}

	public static MethodHandle getConstructor() {
		return constructor;
	}

	public static void setConstructor(MethodHandle constructor) {
		ObjectSerializer.constructor = constructor;
	}
}