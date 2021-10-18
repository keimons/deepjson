package com.keimons.deepjson.util;

import com.keimons.deepjson.AbstractGenerator;
import com.keimons.deepjson.support.generator.CharStringGenerator;
import com.keimons.deepjson.support.generator.SafeStringGenerator;

/**
 * {@link String}生成器帮助类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringGeneratorHelper {

	private static final String BYTES_NAME = "com.keimons.deepjson.support.generator.ByteStringGenerator";

	private static final AbstractGenerator<String> CHARS = new CharStringGenerator();
	private static final AbstractGenerator<String> BYTES;

	static {
		AbstractGenerator<String> instance = null;
		try {
			if (PlatformUtil.javaVersion() < 7) {
				// sun java 1.6 supported compress string.
				instance = new SafeStringGenerator();
			} else if (!CodecUtil.CHARS) {
				Class<?> clazz = Class.forName(BYTES_NAME);
				UnsafeUtil.getUnsafe().ensureClassInitialized(clazz);
				@SuppressWarnings("unchecked")
				AbstractGenerator<String> w = (AbstractGenerator<String>) clazz.getConstructor().newInstance();
				instance = w;
			}
		} catch (Exception e) {
			e.printStackTrace();
			instance = new SafeStringGenerator();
		}
		BYTES = instance;
	}

	/**
	 * 字符串写入工具
	 *
	 * @return 根据当前的java版本，获取一个写入工具
	 */
	public static AbstractGenerator<String> stringGenerator() {
		if (CodecUtil.CHARS) {
			return CHARS;
		} else {
			return BYTES;
		}
	}
}