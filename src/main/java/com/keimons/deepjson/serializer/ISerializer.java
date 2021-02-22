package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
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

	int length(Object object, long options);

	byte coder(Object object, long options);

	/**
	 * 序列化
	 *
	 * @param object  要序列化的对象
	 * @param options 序列化选项
	 * @return 序列化结果
	 */
	String write(Object object, SerializerOptions... options);

	/**
	 * 连接字符串
	 *
	 * @param object 对象
	 * @param buf    新的字节码
	 * @return 写入的byte数量
	 */
	int write(Object object, ByteBuf buf);
}