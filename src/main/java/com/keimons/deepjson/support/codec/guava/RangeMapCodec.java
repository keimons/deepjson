package com.keimons.deepjson.support.codec.guava;

import com.google.common.collect.RangeMap;
import com.keimons.deepjson.*;
import com.keimons.deepjson.support.codec.AbstractClassCodec;

/**
 * Google Guava {@link RangeMap}编解码器 (暂不支持)
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
@Deprecated
public class RangeMapCodec extends AbstractClassCodec<RangeMap> {

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public void build(AbstractContext context, RangeMap value) {

	}

	@Override
	public RangeMap decode(IDecodeContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, RangeMap value, int uniqueId, long options) {

	}
}