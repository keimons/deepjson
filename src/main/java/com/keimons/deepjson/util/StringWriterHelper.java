package com.keimons.deepjson.util;

import com.keimons.deepjson.AbstractWriter;
import com.keimons.deepjson.support.writer.CharStringWriter;
import com.keimons.deepjson.support.writer.SafeStringWriter;

/**
 * java
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class StringWriterHelper {

	private static final String BYTES_NAME = "com.keimons.deepjson.support.writer.ByteStringWriter";

	private static final AbstractWriter<String> CHARS = new CharStringWriter();
	private static final AbstractWriter<String> BYTES;

	static {
		AbstractWriter<String> instance = null;
		try {
			if (PlatformUtil.javaVersion() < 7) {
				// sun java 1.6 supported compress string.
				instance = new SafeStringWriter();
			} else if (!CodecUtil.CHARS) {
				Class<?> clazz = Class.forName(BYTES_NAME);
				UnsafeUtil.getUnsafe().ensureClassInitialized(clazz);
				@SuppressWarnings("unchecked")
				AbstractWriter<String> w = (AbstractWriter<String>) clazz.getConstructor().newInstance();
				instance = w;
			}
		} catch (Exception e) {
			e.printStackTrace();
			instance = new SafeStringWriter();
		}
		BYTES = instance;
	}

	/**
	 * 字符串写入工具
	 *
	 * @return 根据当前的java版本，获取一个写入工具
	 */
	public static AbstractWriter<String> stringWriter() {
		if (CodecUtil.CHARS) {
			return CHARS;
		} else {
			return BYTES;
		}
	}
}