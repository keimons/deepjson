package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.Table;
import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;

/**
 * Google Guava {@link Table}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TableCodec extends AbstractOnlineCodec<Table<?, ?, ?>> {

	public static final TableCodec instance = new TableCodec();

	@Override
	public void build(WriterContext context, Table<?, ?, ?> value) {
		context.build(value.columnMap());
	}

	@Override
	public Table<?, ?, ?> decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Table<?, ?, ?> value, int uniqueId, long options) {
		context.encode(buf, CodecModel.V, options);
	}
}