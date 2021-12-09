package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;

import java.io.IOException;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * {@link DoubleAdder}编解码器
 * <p>
 * 我们将{@link DoubleAdder}识别为一个{@code double}类型值，它不存与循环引用。如果有需要，可以重写这个类。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class DoubleAdderCodec extends KlassCodec<DoubleAdder> {

	public static final DoubleAdderCodec instance = new DoubleAdderCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, DoubleAdder value, int uniqueId, long options) throws IOException {
		if (model == CodecModel.V || CodecOptions.PrimitiveKey.isOptions(options)) {
			writer.write(value.sum());
		} else {
			writer.writeMark('"');
			writer.write(value.sum());
			writer.writeMark('"');
		}
	}

	@Override
	public DoubleAdder decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(SyntaxToken.STRING, SyntaxToken.NUMBER);
		double sum = buf.doubleValue();
		DoubleAdder instance = new DoubleAdder();
		instance.add(sum);
		return instance;
	}
}