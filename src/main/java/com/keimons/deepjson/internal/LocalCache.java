package com.keimons.deepjson.internal;

import com.keimons.deepjson.Generator;
import com.keimons.deepjson.WriterBuffer;
import com.keimons.deepjson.WriterContext;
import com.keimons.deepjson.util.StringGeneratorHelper;

/**
 * 本地缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class LocalCache {

	public WriterBuffer buffer = WriterBuffer.compositeBuffer();

	public WriterContext context = WriterContext.defaultContext();

	public Generator<String> writer = StringGeneratorHelper.stringGenerator();
}