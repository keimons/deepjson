package com.keimons.deepjson;

/**
 * 数组编码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface IArrayEncoder<T> {

	/**
	 * 计算编码后长度
	 *
	 * @param buffers     缓冲区
	 * @param bufferIndex 最后的写入缓冲区
	 * @param writeIndex  最后的写入位置
	 * @return 长度
	 */
	int length(char[][] buffers, int bufferIndex, int writeIndex);

	/**
	 * 对缓冲区进行编码后写入指定缓冲区
	 *
	 * @param buffers     要进行编码的缓冲区
	 * @param bufferIndex 最后的写入缓冲区
	 * @param writeIndex  最后的写入位置
	 * @param dest        编码后的缓冲区
	 * @return 实际写入字节数
	 */
	int encode(char[][] buffers, int bufferIndex, int writeIndex, byte[] dest);

	/**
	 * 对缓冲区进行编码后写入指定缓冲区
	 *
	 * @param buffers     要进行编码的缓冲区
	 * @param bufferIndex 最后的写入缓冲区
	 * @param writeIndex  最后的写入位置
	 * @param dest        编码后的缓冲区
	 * @return 实际写入字节数
	 */
	int encode(char[][] buffers, int bufferIndex, int writeIndex, T dest);

	interface Consumer<T> {

		void accept(T dest, int index, int value);
	}

	final class ByteAdapter implements Consumer<byte[]> {

		public static final ByteAdapter instance = new ByteAdapter();

		@Override
		public void accept(byte[] dest, int index, int value) {
			dest[index] = (byte) value;
		}
	}

	final class ExtAdapter implements Consumer<IAdapter> {

		public static final ExtAdapter instance = new ExtAdapter();

		@Override
		public void accept(IAdapter dest, int index, int value) {
			dest.writeByte(value);
		}
	}
}