package com.keimons.deepjson.serializer;

import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFiller;

/**
 * int[]数组的序列化方案
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class IntegerArraySerializer extends BaseSerializer {

	@Override
	public int length(Object object) {
		int length = 0;
		if (object.getClass() == int[].class) {
			int[] ints = (int[]) object;
			for (int i : ints) {
				length += FillerHelper.size(i);
			}
			if (ints.length > 0) {
				length += ints.length - 1;
			}
		} else {
			Integer[] ints = (Integer[]) object;
			for (int i : ints) {
				length += FillerHelper.size(i);
			}
			if (ints.length > 0) {
				length += ints.length - 1;
			}
		}
		length += 2;
		return length;
	}

	@Override
	public byte coder(Object object) {
		return (byte) (FillerHelper.COMPACT_STRINGS ? 0 : 1);
	}

	@Override
	public int write(Object object, byte[] buf, byte coder, int writeIndex) {
		if (coder == FillerHelper.LATIN) {
			buf[writeIndex++] = '[';
			boolean writeEntry = false;
			if (object.getClass() == int[].class) {
				int[] ints = (int[]) object;
				for (int i : ints) {
					writeEntry = true;
					int length = FillerHelper.size(i);
					writeIndex += length;
					FillerHelper.putLATIN(buf, writeIndex, i);
					buf[writeIndex++] = ',';
				}
			} else {
				Integer[] ints = (Integer[]) object;
				for (int i : ints) {
					writeEntry = true;
					int length = FillerHelper.size(i);
					writeIndex += length;
					FillerHelper.putLATIN(buf, writeIndex, i);
					buf[writeIndex++] = ',';
				}
			}
			if (writeEntry) {
				buf[--writeIndex] = ']';
			} else {
				buf[writeIndex] = ']';
			}
		} else {
			int index = writeIndex << 1;
			buf[index++] = IFiller.UTF16_BRACKET_L[0];
			buf[index] = IFiller.UTF16_BRACKET_L[1];
			writeIndex++;
			boolean writeEntry = false;
			if (object.getClass() == int[].class) {
				int[] ints = (int[]) object;
				for (int i : ints) {
					writeEntry = true;
					int length = FillerHelper.size(i);
					writeIndex += length;
					FillerHelper.putUTF16(buf, writeIndex, i);
					index = writeIndex << 1;
					buf[index++] = IFiller.UTF16_SPLIT[0];
					buf[index] = IFiller.UTF16_SPLIT[1];
					writeIndex++;
				}
			} else {
				Integer[] ints = (Integer[]) object;
				for (int i : ints) {
					writeEntry = true;
					int length = FillerHelper.size(i);
					writeIndex += length;
					FillerHelper.putUTF16(buf, writeIndex, i);
					index = writeIndex << 1;
					buf[index++] = IFiller.UTF16_SPLIT[0];
					buf[index] = IFiller.UTF16_SPLIT[1];
					writeIndex++;
				}
			}
			if (writeEntry) {
				index = (--writeIndex) << 1;
			} else {
				index = writeIndex << 1;
			}
			buf[index++] = IFiller.UTF16_BRACKET_R[0];
			buf[index] = IFiller.UTF16_BRACKET_R[1];
		}
		return buf.length;
	}
}