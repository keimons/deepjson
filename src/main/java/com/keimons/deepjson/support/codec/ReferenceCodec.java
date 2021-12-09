package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ReferenceNode;

import java.io.IOException;

/**
 * {@link ReferenceNode}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReferenceCodec extends AbstractOnlineCodec<ReferenceNode> {

	public static final ReferenceCodec instance = new ReferenceCodec();

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, ReferenceNode value, int uniqueId, long options) throws IOException {
		writer.writeMark('\"');
		writer.writeMark('$');
		writer.writeMark('i');
		writer.writeMark('d');
		writer.writeMark(':');
		writer.write(value.getUnique());
		writer.writeMark('\"');
	}

	@Override
	public ReferenceNode decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		throw new UnsupportedOperationException();
	}
}