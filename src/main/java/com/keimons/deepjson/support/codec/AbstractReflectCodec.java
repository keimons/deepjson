package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.TypeNotFoundException;

/**
 * 其他类型编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractReflectCodec implements ICodec<Object> {

	@Override
	public CodecType getCodecType() {
		return CodecType.DECODE;
	}

	@Override
	public boolean isSearch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void build(AbstractContext context, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		throw new UnsupportedOperationException();
	}

	/**
	 * 类型检测
	 *
	 * @param context 上下文
	 * @param buf     缓冲区
	 * @param options 解码选项
	 * @return 类型
	 */
	protected Class<?> typeCheck(IDecodeContext context, ReaderBuffer buf, long options) {
		SyntaxToken token = buf.token();
		if (token == SyntaxToken.STRING && buf.checkGetType()) {
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.COLON); // 预期当前语法是 ":"
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.STRING);
			String type = buf.stringValue();
			buf.nextToken(); // 读取下一个token
			try {
				return Class.forName(type);
			} catch (ClassNotFoundException e) {
				throw new TypeNotFoundException(e);
			}
		}
		return null;
	}
}