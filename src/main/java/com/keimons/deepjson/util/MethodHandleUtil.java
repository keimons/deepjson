package com.keimons.deepjson.util;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * {@link MethodHandle}工具类，用于提供可信任的{@link MethodHandles.Lookup}。
 * <p>
 * 这个工具类不对外开放使用。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class MethodHandleUtil extends ReflectionUtil {

	private static final MethodHandles.Lookup lookup;

	static {
		MethodHandles.Lookup lookup0 = null;
		if (PlatformUtil.javaVersion() >= 9) {
			Unsafe unsafe = UnsafeUtil.getUnsafe();
			try {
				// 尝试查找私有的 MethodHandles.Lookup#IMPL_LOOKUP
				long offset = unsafe.staticFieldOffset(MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP"));
				lookup0 = (MethodHandles.Lookup) unsafe.getObject(MethodHandles.Lookup.class, offset);
			} catch (Exception e) {
				try {
					// 查找失败 尝试自己生成一个具有所有权限的lookup。
					lookup0 = (MethodHandles.Lookup) unsafe.allocateInstance(MethodHandles.Lookup.class);
					long offset1 = unsafe.staticFieldOffset(MethodHandles.Lookup.class.getDeclaredField("lookupClass"));
					long offset2 = unsafe.staticFieldOffset(MethodHandles.Lookup.class.getDeclaredField("allowedModes"));
					unsafe.putObject(lookup0, offset1, Object.class);
					unsafe.putInt(lookup0, offset2, -1);
				} catch (Exception x) {
					x.printStackTrace();
				}
			}
		}
		lookup = lookup0;
	}

	/**
	 * 可信任的{@link MethodHandles.Lookup}。
	 * <p>
	 * 这是一个可信任的Lookup，它具有所有的访问权限，请慎用。
	 *
	 * @return 可信任的{@link MethodHandles.Lookup}
	 */
	public static MethodHandles.Lookup Lookup() {
		if (lookup == null) {
			throw new IllegalVersionException(9);
		}
		return lookup;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T newInstance0(Class<?> clazz) throws Throwable {
		MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
		return (T) constructor.invoke();
	}
}