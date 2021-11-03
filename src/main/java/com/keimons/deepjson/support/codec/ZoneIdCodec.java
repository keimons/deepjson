package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.time.ZoneId;

/**
 * {@link ZoneId}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class ZoneIdCodec extends AbstractOnlineCodec<ZoneId> {

	public static final ZoneIdCodec instance = new ZoneIdCodec();

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, ZoneId zoneId, int uniqueId, long options) {
		buf.writeWithQuote(zoneId.getId());
	}

	@Override
	public ZoneId decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}
}