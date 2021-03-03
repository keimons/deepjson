package com.keimons.deepjson.serializer;

import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

public interface ISerializer {

	Unsafe unsafe = UnsafeUtil.getUnsafe();

	default void link() {

	}

	int length(Object object, long options);

	byte coder(Object object, long options);

	void write(Object object, ByteBuf buf);
}