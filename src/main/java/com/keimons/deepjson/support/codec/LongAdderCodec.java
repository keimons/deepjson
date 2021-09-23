package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.IDecodeContext;
import com.keimons.deepjson.ReaderBuffer;
import com.keimons.deepjson.support.SyntaxToken;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.LongAdder;

/**
 * {@link LongAdder}编解码器
 * <p>
 * 我们将{@link LongAdder}识别为一个{@code long}类型值，它不存与循环引用。如果有需要，可以重写这个类。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.8
 **/
public class LongAdderCodec extends BaseCodec<LongAdder> {

	public static final LongAdderCodec instance = new LongAdderCodec();

	@Override
	public boolean isSearch() {
		return false;
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, LongAdder value, int uniqueId, long options) {
		buf.write(value.sum());
	}

	@Override
	public LongAdder decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {
		buf.assertExpectedSyntax(SyntaxToken.STRING, SyntaxToken.NUMBER);
		int sum = buf.intValue();
		LongAdder instance = new LongAdder();
		instance.add(sum);
		return instance;
	}
}