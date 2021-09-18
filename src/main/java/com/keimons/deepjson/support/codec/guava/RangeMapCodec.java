package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.RangeMap;
import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.support.codec.BaseCodec;

/**
 * Google Guava {@link RangeMap}编解码器 (暂不支持)
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
@Deprecated
public class RangeMapCodec extends BaseCodec<RangeMap> {

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public void build(AbstractContext context, RangeMap value) {

	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, RangeMap value, int uniqueId, long options) {

	}
}