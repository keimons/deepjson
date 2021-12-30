package com.keimons.deepjson.internal.util;

import com.google.common.collect.Sets;
import com.keimons.deepjson.support.codec.extended.InlineCodec;
import com.keimons.deepjson.support.transcoder.ByteStringTranscoder;
import com.keimons.deepjson.util.IllegalCallerException;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.ReflectionUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Set;

/**
 * {@link MethodHandle}工具类，用于提供可信任的{@link MethodHandles.Lookup}。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class LookupUtil {

	private static final Set<String> WHITE = Collections.unmodifiableSet(Sets.newHashSet(
			ByteStringTranscoder.class.getName(),
			InlineCodec.class.getName(),
			ReflectionUtil.class.getName(),
			"com.keimons.deepjson.test.util.LookupUtilTest" // for test
	));

	private static final MethodHandles.Lookup lookup;

	static {
		MethodHandles.Lookup lookup0 = null;
		if (PlatformUtil.javaVersion() >= 9) {
			Unsafe unsafe = UnsafeUtil.getUnsafe();
			try {
				// 尝试查找受信任的包级私有的 MethodHandles.Lookup#IMPL_LOOKUP
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
	 * 这是一个可信任的{@code Lookup}，它具有所有的访问权限，慎用。
	 *
	 * @return 可信任的{@link MethodHandles.Lookup}
	 */
	public static MethodHandles.Lookup lookup() {
		if (lookup == null) {
			throw new IllegalCallerException(9);
		}
		Class<?> caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		if (!WHITE.contains(caller.getName())) {
			throw new IllegalCallerException(caller);
		}
		return lookup;
	}
}