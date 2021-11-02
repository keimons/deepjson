package com.keimons.deepjson;

import com.keimons.deepjson.charset.UTF_8;

/**
 * 预设字符集
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class Charsets {

	public static final ITranscoder<byte[]> UTF_8 = new UTF_8<byte[]>();
}