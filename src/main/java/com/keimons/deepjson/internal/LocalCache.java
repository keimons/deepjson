package com.keimons.deepjson.internal;

import com.keimons.deepjson.Buffer;
import com.keimons.deepjson.Generator;
import com.keimons.deepjson.JsonWriter;
import com.keimons.deepjson.WriterContext;
import com.keimons.deepjson.internal.buffer.CompositeBuffer;
import com.keimons.deepjson.util.StringGeneratorHelper;

/**
 * 本地缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class LocalCache {

	public JsonWriter writer = JsonWriter.defaultWriter();

	public Buffer buf = new CompositeBuffer();

	public WriterContext context = WriterContext.defaultContext();

	public Generator<String> gen = StringGeneratorHelper.stringGenerator();
}