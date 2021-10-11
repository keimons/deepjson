package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.TypeNotFoundException;

/**
 * 幻影编解码器
 * <p>
 * 用于解析类型参数、参数化类型、通配符、泛型数组。幻影编解码器起到占位的作用，其中，
 * 参数化类型用于记录原始类型，方便在上下文环境中查找真正的类型。DeepJson依赖于幻影
 * 编解码器实现对于泛型类型的解码支持，是设计的核心一环。幻影编解码器不参与解码，仅仅
 * 提供解码泛型时所需要的类型信息。
 * <pre>
 *     class Node {
 *         Map&lt;String, Integer&gt; value;
 *     }
 * </pre>
 * <pre>
 * +---- ------+       +------------------------+       +----------+
 * | NodeCodec | <---- | ParameterizedTypeCodec | <---- | MapCodec |
 * +-----------+       +------------------------+       +----------+
 * </pre>
 * 通过插入幻影编解码器，实现对象解析时泛型类型的查找。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class PhantomCodec implements ICodec<Object> {

	@Override
	public final CodecType getCodecType() {
		return CodecType.DECODE;
	}

	@Override
	public final boolean isSearch() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void build(AbstractContext context, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
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