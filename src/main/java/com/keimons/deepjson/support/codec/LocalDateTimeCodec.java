package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * {@link LocalDateTime}编解码器
 * <p>
 * 因为找不到合适的编解码方案，所以，{@link LocalDateTimeCodec}不采用任何格式。
 * 例如：
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class LocalDateTimeCodec extends AbstractOnlineCodec<LocalDateTime> {

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, LocalDateTime value, int uniqueId, long options) throws IOException {
		writer.write(value.toString());
	}

	@Override
	public LocalDateTime decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		return null;
	}
}