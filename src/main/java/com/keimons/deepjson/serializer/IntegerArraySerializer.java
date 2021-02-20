package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFiller;

/**
 * Integer[]序列化方案
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class IntegerArraySerializer extends BaseSerializer {

	@Override
	public int length(Object object, long options) {
		if (object == null) {
			if (SerializerOptions.IgnoreNonField.isOptions(options)) {
				return 4; // "null"
			} else {
				return 0;
			}
		}
		int length = 0;
		Integer[] ints = (Integer[]) object;
		for (Integer i : ints) {
			if (i == null) {
				length += 4; // null
			} else {
				length += FillerHelper.size(i);
			}
		}
		if (ints.length > 0) {
			length += ints.length - 1; // ","
		}
		length += 2;
		return length;
	}

	@Override
	public byte coder(Object object, long options) {
		return (byte) (FillerHelper.COMPACT_STRINGS ? 0 : 1);
	}

	@Override
	public int write(Object object, byte[] buf, byte coder, int writeIndex, long options) {
		Integer[] value = (Integer[]) object;
		int writeLength = 2;
		if (coder == FillerHelper.LATIN) {
			buf[writeIndex++] = '[';
			for (Integer i : value) {
				if (i == null) {
					buf[writeIndex++] = 'n';
					buf[writeIndex++] = 'u';
					buf[writeIndex++] = 'l';
					buf[writeIndex++] = 'l';
					writeLength += 4;
				} else {
					int length = FillerHelper.size(i);
					writeIndex += length;
					writeLength += length;
					FillerHelper.putLATIN(buf, writeIndex, i);
				}
				buf[writeIndex++] = ',';
			}
			if (value.length > 0) {
				buf[--writeIndex] = ']';
				writeLength += value.length - 1;
			} else {
				buf[writeIndex] = ']';
			}
		} else {
			int index = writeIndex << 1;
			buf[index++] = IFiller.UTF16_BRACKET_L[0];
			buf[index] = IFiller.UTF16_BRACKET_L[1];
			writeIndex++;
			for (Integer i : value) {
				if (i == null) {
					FillerHelper.putChar2(buf, writeIndex++, 'n');
					FillerHelper.putChar2(buf, writeIndex++, 'u');
					FillerHelper.putChar2(buf, writeIndex++, 'l');
					FillerHelper.putChar2(buf, writeIndex++, 'l');
					writeLength += 4;
				} else {
					int length = FillerHelper.size(i);
					writeIndex += length;
					writeLength += length;
					FillerHelper.putUTF16(buf, writeIndex, i);
				}
				index = writeIndex << 1;
				buf[index++] = IFiller.UTF16_SPLIT[0];
				buf[index] = IFiller.UTF16_SPLIT[1];
				writeIndex++;
			}
			if (value.length > 0) {
				index = (--writeIndex) << 1;
				writeLength += value.length - 1;
			} else {
				index = writeIndex << 1;
			}
			buf[index++] = IFiller.UTF16_BRACKET_R[0];
			buf[index] = IFiller.UTF16_BRACKET_R[1];
		}
		return writeLength;
	}
}