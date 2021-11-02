package com.keimons.deepjson.util;

import com.keimons.deepjson.Generator;
import com.keimons.deepjson.ITranscoder;
import com.keimons.deepjson.adapter.NoneConverter;
import com.keimons.deepjson.support.transcoder.CharStringTranscoder;
import com.keimons.deepjson.support.transcoder.SafeStringTranscoder;

/**
 * {@link String}生成器帮助类
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringGeneratorHelper {

	private static final String BYTES_NAME = "com.keimons.deepjson.support.transcoder.ByteStringTranscoder";

	public static final Generator<String> SAFE = new Generator<String>(SafeStringTranscoder.instance, new NoneConverter<String>());
	private static final Generator<String> CHAR = new Generator<String>(CharStringTranscoder.instance, new NoneConverter<String>());
	private static final Generator<String> BYTE;

	static {
		ITranscoder<String> instance = null;
		try {
			if (PlatformUtil.javaVersion() < 7) {
				// sun java 1.6 supported compress string.
				instance = SafeStringTranscoder.instance;
			} else if (!CodecUtil.CHARS) {
				Class<?> clazz = Class.forName(BYTES_NAME);
				UnsafeUtil.getUnsafe().ensureClassInitialized(clazz);
				@SuppressWarnings("unchecked")
				ITranscoder<String> w = (ITranscoder<String>) clazz.getConstructor().newInstance();
				instance = w;
			}
		} catch (Exception e) {
			e.printStackTrace();
			instance = SafeStringTranscoder.instance;
		}
		BYTE = new Generator<String>(instance, new NoneConverter<String>());
	}

	/**
	 * 字符串写入工具
	 *
	 * @return 根据当前的java版本，获取一个写入工具
	 */
	public static Generator<String> stringGenerator() {
		if (CodecUtil.CHARS) {
			return CHAR;
		} else {
			return BYTE;
		}
	}
}