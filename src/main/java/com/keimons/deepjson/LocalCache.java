package com.keimons.deepjson;

import com.keimons.deepjson.util.StringGeneratorHelper;

/**
 * 本地缓存
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
class LocalCache {

	WriterBuffer buffer = WriterBuffer.compositeBuffer();

	WriterContext context = WriterContext.defaultContext();

	Generator<String> writer = StringGeneratorHelper.stringGenerator();
}