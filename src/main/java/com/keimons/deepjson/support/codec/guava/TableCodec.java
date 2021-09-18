package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.Table;
import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.support.codec.BaseCodec;

/**
 * Google Guava {@link Table}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class TableCodec extends BaseCodec<Table<?, ?, ?>> {

	public static final TableCodec instance = new TableCodec();

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public void build(AbstractContext context, Table<?, ?, ?> value) {
		context.build(value.columnMap());
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, Table<?, ?, ?> value, int uniqueId, long options) {
		context.encode(buf, options);
	}
}