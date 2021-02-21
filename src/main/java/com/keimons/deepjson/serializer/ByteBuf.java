package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFieldName;
import sun.misc.Unsafe;

public class ByteBuf {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_L_BRACES = (byte) ('{' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACES = (byte) ('{' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACES = (byte) ('}' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACES = (byte) ('}' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_L_BRACKET = (byte) ('[' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACKET = (byte) ('[' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACKET = (byte) (']' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACKET = (byte) (']' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_E = (byte) ('e' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_E = (byte) ('e' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_L = (byte) ('l' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L = (byte) ('l' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_N = (byte) ('n' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_N = (byte) ('n' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_R = (byte) ('r' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R = (byte) ('r' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_T = (byte) ('t' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_T = (byte) ('t' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_U = (byte) ('u' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_U = (byte) ('u' << FillerHelper.LO_BYTE_SHIFT);

	private final long options;

	private byte[] buf;

	private byte coder;

	private int writeIndex;

	private ByteBuf(long options, int capacity, byte coder) {
		this.buf = new byte[capacity << coder];
		this.options = options;
		this.coder = coder;
	}

	public int writeInt(IFieldName field, int value) {
		int length = FillerHelper.size(value);
		int writable = field.size() + length;
		ensureWritable(writable);
		if (coder == FillerHelper.LATIN) {
			for (byte b : field.getFieldNameByLatin()) {
				buf[writeIndex++] = b;
			}
			writeIndex += length;
			FillerHelper.putLATIN(buf, writeIndex, value);
			if (SerializerOptions.IncludeClassName.isOptions(options)) {
				buf[writeIndex++] = 'I';
			}
			buf[writeIndex] = ',';
		} else {
			for (int i = 0; i < field.getFieldNameByUtf16().length; i++) {

			}
			for (byte b : field.getFieldNameByLatin()) {
				buf[writeIndex++] = b;
			}
			writeIndex += length;
			FillerHelper.putUTF16(buf, writeIndex, value);
			buf[writeIndex++] = HI_BYTE_L_BRACES;
			buf[writeIndex] = LO_BYTE_L_BRACES;
		}
		return writable;
	}

	public int writeNumber(IFieldName filler, Number object) {
		long value = object.longValue();
		int length = FillerHelper.size(value);
		int writable = filler.size() + length;
		ensureWritable(writable);
		if (coder == FillerHelper.LATIN) {
			for (byte b : filler.getFieldNameByLatin()) {
				buf[writeIndex++] = b;
			}
			writeIndex += length;
			FillerHelper.putLATIN(buf, writeIndex, value);
			if (SerializerOptions.IncludeClassName.isOptions(options)) {
				buf[writeIndex++] = 'L';
			}
			buf[writeIndex] = ',';
		} else {
			FillerHelper.putUTF16(buf, writeIndex, value);
			buf[writeIndex++] = HI_BYTE_L_BRACES;
			buf[writeIndex] = LO_BYTE_L_BRACES;
		}
		return writable;
	}

	public int writeStartObject() {
		ensureWritable(1);
		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
		if (coder == FillerHelper.LATIN) {
			buf[++writeIndex] = '{';
		} else {
			buf[++writeIndex] = HI_BYTE_L_BRACES;
			buf[++writeIndex] = LO_BYTE_L_BRACES;
		}
		this.writeIndex += 1;
		return 1;
	}

	/**
	 * 写入对象结尾标识。
	 *
	 * @param override 是否重写最后一个字符
	 * @return 写入字符数量
	 */
	public int writeEndObject(boolean override) {
		if (override) {
			writeIndex--;
		} else {
			ensureWritable(1);
		}
		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
		if (coder == FillerHelper.LATIN) {
			buf[++writeIndex] = '}';
		} else {
			buf[++writeIndex] = HI_BYTE_R_BRACES;
			buf[++writeIndex] = LO_BYTE_R_BRACES;
		}
		this.writeIndex += 1;
		return override ? 0 : 1;
	}

	public int writeStartArray() {
		ensureWritable(1);
		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
		if (coder == FillerHelper.LATIN) {
			buf[++writeIndex] = '[';
		} else {
			buf[++writeIndex] = HI_BYTE_L_BRACKET;
			buf[++writeIndex] = LO_BYTE_L_BRACKET;
		}
		this.writeIndex += 1;
		return 1;
	}

	/**
	 * 写入数组结尾标识。
	 *
	 * @param override 是否重写最后一个字符
	 * @return 写入字符数量
	 */
	public int writeEndArray(boolean override) {
		if (override) {
			writeIndex--;
		} else {
			ensureWritable(1);
		}
		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
		if (coder == FillerHelper.LATIN) {
			buf[++writeIndex] = ']';
		} else {
			buf[++writeIndex] = HI_BYTE_R_BRACKET;
			buf[++writeIndex] = LO_BYTE_R_BRACKET;
		}
		this.writeIndex += 1;
		return override ? 0 : 1;
	}

//	public int writeTrue() {
//		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
//		if (coder == FillerHelper.LATIN) {
//			buf[++writeIndex] = 't';
//			buf[++writeIndex] = 'r';
//			buf[++writeIndex] = 'u';
//			buf[++writeIndex] = 'e';
//		} else {
//			buf[++writeIndex] = HI_BYTE_T;
//			buf[++writeIndex] = LO_BYTE_T;
//			buf[++writeIndex] = HI_BYTE_R;
//			buf[++writeIndex] = LO_BYTE_R;
//			buf[++writeIndex] = HI_BYTE_U;
//			buf[++writeIndex] = LO_BYTE_U;
//			buf[++writeIndex] = HI_BYTE_E;
//			buf[++writeIndex] = LO_BYTE_E;
//		}
//		this.writeIndex += 4;
//		return 4;
//	}
//
//	public int writeFalse() {
//		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
//		if (coder == FillerHelper.LATIN) {
//			buf[++writeIndex] = 'f';
//			buf[++writeIndex] = 'a';
//			buf[++writeIndex] = 'l';
//			buf[++writeIndex] = 's';
//			buf[++writeIndex] = 'e';
//		} else {
//			buf[++writeIndex] = HI_BYTE_F;
//			buf[++writeIndex] = LO_BYTE_F;
//			buf[++writeIndex] = HI_BYTE_A;
//			buf[++writeIndex] = LO_BYTE_A;
//			buf[++writeIndex] = HI_BYTE_L;
//			buf[++writeIndex] = LO_BYTE_L;
//			buf[++writeIndex] = HI_BYTE_S;
//			buf[++writeIndex] = LO_BYTE_S;
//			buf[++writeIndex] = HI_BYTE_E;
//			buf[++writeIndex] = LO_BYTE_E;
//		}
//		this.writeIndex += 5;
//		return 5;
//	}

	public int writeNull() {
		ensureWritable(4);
		int writeIndex = (this.writeIndex << coder) - 1; // 强迫症害死人，抖个机灵算了
		if (coder == FillerHelper.LATIN) {
			buf[++writeIndex] = 'n';
			buf[++writeIndex] = 'u';
			buf[++writeIndex] = 'l';
			buf[++writeIndex] = 'l';
		} else {
			buf[++writeIndex] = HI_BYTE_N;
			buf[++writeIndex] = LO_BYTE_N;
			buf[++writeIndex] = HI_BYTE_U;
			buf[++writeIndex] = LO_BYTE_U;
			buf[++writeIndex] = HI_BYTE_L;
			buf[++writeIndex] = LO_BYTE_L;
			buf[++writeIndex] = HI_BYTE_L;
			buf[++writeIndex] = LO_BYTE_L;
		}
		this.writeIndex += 4;
		return 4;
	}

	/**
	 * 确保缓冲区的编码方式与即将写入的编码方式相同。
	 *
	 * @param coder 即将写入的编码方式
	 */
	public void ensureCoder(byte coder) {
		if (this.coder != coder) {
			throw new CoderModificationException();
		}
	}

	/**
	 * 确保缓冲区的可写入字节数大于或等于即将写入的字节数。
	 *
	 * @param writableBytes 即将写入的字节数
	 */
	public void ensureWritable(int writableBytes) {
		if (writableBytes + writeIndex > buf.length) {
			if (true) {
				expandCapacity(writableBytes + writeIndex);
			} else {
				throw new CapacityModificationException();
			}
		}
	}

	/**
	 * 扩容
	 *
	 * @param minCapacity 最小容量
	 */
	private void expandCapacity(int minCapacity) {
		int newCapacity = (buf.length >> 1);

		if (newCapacity < minCapacity) {
			newCapacity = minCapacity;
		}
		byte[] newBuf = new byte[newCapacity];
		System.arraycopy(buf, 0, newBuf, 0, writeIndex);
		buf = newBuf;
	}

	@Override
	public String toString() {
		try {
			String str = (String) unsafe.allocateInstance(String.class);
			unsafe.putObject(str, FillerHelper.VALUE_OFFSET_STRING, buf);
			unsafe.putByte(str, FillerHelper.CODER_OFFSET_STRING, coder);
			return str;
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ByteBuf buffer(long options, int initCapacity, byte coder) {
		return new ByteBuf(options, initCapacity, coder);
	}
}