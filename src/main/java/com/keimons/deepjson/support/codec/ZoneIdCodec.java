package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.CodecModel;

import java.time.ZoneId;

/**
 * {@link ZoneId}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class ZoneIdCodec extends BaseCodec<ZoneId> {

	public static final ZoneIdCodec instance = new ZoneIdCodec();

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, ZoneId zoneId, int uniqueId, long options) {
		buf.writeWithQuote(zoneId.getId());
	}
}