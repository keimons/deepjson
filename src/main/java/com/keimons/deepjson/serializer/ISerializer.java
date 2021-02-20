package com.keimons.deepjson.serializer;

import com.keimons.deepjson.filler.FillerHelper;

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

	int length(Object object);

	byte coder(Object object);

	String write(Object object);

	/**
	 * 连接字符串
	 *
	 * @param object     对象
	 * @param buf        新的字节码
	 * @param coder      编码方式
	 * @param writeIndex 写入位置
	 * @return 写入的byte数量
	 */
	int write(Object object, byte[] buf, byte coder, int writeIndex);
}