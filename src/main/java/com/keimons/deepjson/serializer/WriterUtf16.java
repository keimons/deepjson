package com.keimons.deepjson.serializer;

import com.keimons.deepjson.UnsafeUtil;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFieldName;
import sun.misc.Unsafe;

/**
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
class WriterUtf16 implements IWriter<byte[]> {

	private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

	private static final byte HI_BYTE_L_BRACES = (byte) ('{' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACES = (byte) ('{' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACES = (byte) ('}' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACES = (byte) ('}' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_L_BRACKET = (byte) ('[' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L_BRACKET = (byte) ('[' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_R_BRACKET = (byte) (']' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R_BRACKET = (byte) (']' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK1 = (byte) (',' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK1 = (byte) (',' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_MARK = (byte) ('"' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_MARK = (byte) ('"' << FillerHelper.LO_BYTE_SHIFT);

	private static final byte HI_BYTE_A = (byte) ('a' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_A = (byte) ('a' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_E = (byte) ('e' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_E = (byte) ('e' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_F = (byte) ('f' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_F = (byte) ('f' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_L = (byte) ('l' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_L = (byte) ('l' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_N = (byte) ('n' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_N = (byte) ('n' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_R = (byte) ('r' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_R = (byte) ('r' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_S = (byte) ('s' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_S = (byte) ('s' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_T = (byte) ('t' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_T = (byte) ('t' << FillerHelper.LO_BYTE_SHIFT);
	private static final byte HI_BYTE_U = (byte) ('u' << FillerHelper.HI_BYTE_SHIFT);
	private static final byte LO_BYTE_U = (byte) ('u' << FillerHelper.LO_BYTE_SHIFT);

	@Override
	public int writeStartObject(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return 1;
	}

	@Override
	public int writeEndObject(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_R_BRACES;
		buf[writeIndex] = LO_BYTE_R_BRACES;
		return 1;
	}

	@Override
	public int writeStartArray(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_L_BRACKET;
		buf[writeIndex] = LO_BYTE_L_BRACKET;
		return 1;
	}

	@Override
	public int writeEndArray(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_R_BRACKET;
		buf[writeIndex] = LO_BYTE_R_BRACKET;
		return 1;
	}

	@Override
	public int writeMark(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_MARK1;
		buf[writeIndex] = LO_BYTE_MARK1;
		return 1;
	}

	@Override
	public int writeNull(byte[] buf, long options, int writeIndex) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_N;
		buf[writeIndex++] = LO_BYTE_N;
		buf[writeIndex++] = HI_BYTE_U;
		buf[writeIndex++] = LO_BYTE_U;
		buf[writeIndex++] = HI_BYTE_L;
		buf[writeIndex++] = LO_BYTE_L;
		buf[writeIndex++] = HI_BYTE_L;
		buf[writeIndex] = LO_BYTE_L;
		return 4;
	}

	@Override
	public int writeField(byte[] buf, long options, int writeIndex, IFieldName filler) {
		writeIndex <<= 1;
		for (byte b : filler.getFieldNameByUtf16()) {
			buf[writeIndex++] = b;
		}
		return filler.size() - 1;
	}

	@Override
	public int writeBoolean(byte[] buf, long options, int writeIndex, boolean value) {
		writeIndex <<= 1;
		if (value) {
			buf[writeIndex++] = HI_BYTE_T;
			buf[writeIndex++] = LO_BYTE_T;
			buf[writeIndex++] = HI_BYTE_U;
			buf[writeIndex++] = LO_BYTE_U;
			buf[writeIndex++] = HI_BYTE_R;
			buf[writeIndex++] = LO_BYTE_R;
			buf[writeIndex++] = HI_BYTE_E;
			buf[writeIndex++] = LO_BYTE_E;
		} else {
			buf[writeIndex++] = HI_BYTE_F;
			buf[writeIndex++] = LO_BYTE_F;
			buf[writeIndex++] = HI_BYTE_A;
			buf[writeIndex++] = LO_BYTE_A;
			buf[writeIndex++] = HI_BYTE_L;
			buf[writeIndex++] = LO_BYTE_L;
			buf[writeIndex++] = HI_BYTE_S;
			buf[writeIndex++] = LO_BYTE_S;
			buf[writeIndex++] = HI_BYTE_E;
			buf[writeIndex++] = LO_BYTE_E;
		}
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return value ? 5 : 6;
	}

	@Override
	public int writeChar(byte[] buf, long options, int writeIndex, char value) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
		buf[writeIndex++] = (byte) (value >> FillerHelper.HI_BYTE_SHIFT);
		buf[writeIndex++] = (byte) (value >> FillerHelper.LO_BYTE_SHIFT);
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return 4;
	}

	@Override
	public int writeInt(byte[] buf, long options, int writeIndex, int value) {
		FillerHelper.putUTF16(buf, writeIndex, value);
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return 1;
	}

	@Override
	public int writeLong(byte[] buf, long options, int writeIndex, long value) {
		FillerHelper.putUTF16(buf, writeIndex, value);
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_MARK1;
		buf[writeIndex] = LO_BYTE_MARK1;
		return 1;
	}

	@Override
	public int writeString(byte[] buf, long options, int writeIndex, String value) {
		writeIndex <<= 1;
		byte coder = unsafe.getByte(value, FillerHelper.CODER_OFFSET_STRING);
		byte[] bytes = (byte[]) unsafe.getObject(value, FillerHelper.VALUE_OFFSET_STRING);
		if (coder == FillerHelper.LATIN) {
			for (byte b : bytes) {
				buf[writeIndex++] = (byte) (b >> FillerHelper.HI_BYTE_SHIFT);
				buf[writeIndex++] = (byte) (b >> FillerHelper.LO_BYTE_SHIFT);
			}
		} else {
			for (byte b : bytes) {
				buf[writeIndex++] = b;
			}
		}
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return value.length() + 1;
	}

	@Override
	public int writeStringWithMark(byte[] buf, long options, int writeIndex, String value) {
		writeIndex <<= 1;
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
		byte coder = unsafe.getByte(value, FillerHelper.CODER_OFFSET_STRING);
		byte[] bytes = (byte[]) unsafe.getObject(value, FillerHelper.VALUE_OFFSET_STRING);
		if (coder == FillerHelper.LATIN) {
			for (byte b : bytes) {
				buf[writeIndex++] = (byte) (b >> FillerHelper.HI_BYTE_SHIFT);
				buf[writeIndex++] = (byte) (b >> FillerHelper.LO_BYTE_SHIFT);
			}
		} else {
			for (byte b : bytes) {
				buf[writeIndex++] = b;
			}
		}
		buf[writeIndex++] = HI_BYTE_MARK;
		buf[writeIndex++] = LO_BYTE_MARK;
		buf[writeIndex++] = HI_BYTE_L_BRACES;
		buf[writeIndex] = LO_BYTE_L_BRACES;
		return value.length() + 3;
	}

	@Override
	public int writeInts(byte[] buf, long options, int writeIndex, int[] values) {
		return 0;
	}

	@Override
	public int writeInts(byte[] buf, long options, int writeIndex, Integer[] values) {
		return 0;
	}
}