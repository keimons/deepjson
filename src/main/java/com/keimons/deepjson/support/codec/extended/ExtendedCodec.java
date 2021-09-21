package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.compiler.ExtendedCodecClassLoader;
import com.keimons.deepjson.support.codec.BaseCodec;

import java.lang.reflect.Field;

/**
 * 拓展编解码器
 * <p>
 * 将所有的非系统提供的编解码方案识别为第三方拓展，所有的拓展都应该继承自{@link ExtendedCodec}。
 * 所有拓展使用{@code com.keimons.deepjson.support.codec.extended}路径。
 * <p>
 * 这个类存在的意义是确保这个包能被初始化，保证第三方拓展编解码方案能被识别为DeepJson的模块。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see ExtendedCodecClassLoader 拓展编解码方案装载器
 * @since 1.6
 **/
public abstract class ExtendedCodec extends BaseCodec<Object> {

	public abstract void init(Class<?> clazz);

	protected <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Field findField(Class<?> clazz, String fieldName, boolean isPublic) {
		try {
			if (isPublic) {
				return clazz.getField(fieldName);
			} else {
				return clazz.getDeclaredField(fieldName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}