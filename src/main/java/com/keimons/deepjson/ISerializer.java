package com.keimons.deepjson;

import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFiller;

/**
 * 序列化工具
 */
public interface ISerializer {

	byte[] UTF16_L = {
			(byte) ('{' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('{' >> FillerHelper.LO_BYTE_SHIFT),
	};

	byte[] UTF16_R = {
			(byte) ('}' >> FillerHelper.HI_BYTE_SHIFT),
			(byte) ('}' >> FillerHelper.LO_BYTE_SHIFT),
	};

	int size(Object object);

	byte coder(Object object);

	String concat(Object object);

	/**
	 * 连接字符串
	 *
	 * @param object     对象
	 * @param value      新的字节码
	 * @param coder      编码方式
	 * @param writeIndex 写入位置
	 * @return 本次写入的byte数量
	 */
	int concat(Object object, byte[] value, byte coder, int writeIndex);

	void addLast(IFiller filler);

	void removeLast();
}