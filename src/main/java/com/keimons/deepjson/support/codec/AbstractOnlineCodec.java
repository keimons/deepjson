package com.keimons.deepjson.support.codec;

/**
 * Online编解码器
 * <p>
 * 没有特殊的限制，既参与循环查找又参与类型查找的编解码。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractOnlineCodec<T> extends AbstractRawCodec<T> {

	@Override
	public boolean isSearch() {
		return true;
	}

	@Override
	public boolean isCacheType() {
		return true;
	}
}