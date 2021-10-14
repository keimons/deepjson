package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ReferenceNode;

/**
 * {@link ReferenceNode}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReferenceCodec extends AbstractClassCodec<ReferenceNode> {

	public static final ReferenceCodec instance = new ReferenceCodec();

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, ReferenceNode value, int uniqueId, long options) {
		buf.writeMark('\"');
		buf.writeMark('$');
		buf.writeMark('i');
		buf.writeMark('d');
		buf.writeMark(':');
		buf.write(value.getUnique());
		buf.writeMark('\"');
	}

	@Override
	public ReferenceNode decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		throw new UnsupportedOperationException();
	}
}