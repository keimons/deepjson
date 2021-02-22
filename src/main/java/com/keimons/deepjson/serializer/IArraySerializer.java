package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.filler.FillerHelper;

/**
 * 适用int[]的序列化方案
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class IArraySerializer extends BaseSerializer {

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
		int[] ints = (int[]) object;
		for (int i : ints) {
			length += FillerHelper.size(i);
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
	public int write(Object object, ByteBuf buf) {
		int[] value = (int[]) object;
		int writeLength = 2;
		buf.writeStartArray();
//		int markIndex = buf.getWriteIndex();
//		buf.writeInts();
//		buf.writeEndArray();
//		if (coder == FillerHelper.LATIN) {
//			buf[writeIndex++] = '[';
//			for (int i : value) {
//				int length = FillerHelper.size(i);
//				writeIndex += length;
//				writeLength += length;
//				FillerHelper.putLATIN(buf, writeIndex, i);
//				buf[writeIndex++] = ',';
//			}
//			if (value.length > 0) {
//				buf[--writeIndex] = ']';
//				writeLength += value.length - 1;
//			} else {
//				buf[writeIndex] = ']';
//			}
//		} else {
//			int index = writeIndex << 1;
//			buf[index++] = IFiller.UTF16_BRACKET_L[0];
//			buf[index] = IFiller.UTF16_BRACKET_L[1];
//			writeIndex++;
//			for (int i : value) {
//				int length = FillerHelper.size(i);
//				writeIndex += length;
//				writeLength += length;
//				FillerHelper.putUTF16(buf, writeIndex, i);
//				index = writeIndex << 1;
//				buf[index++] = IFiller.UTF16_SPLIT[0];
//				buf[index] = IFiller.UTF16_SPLIT[1];
//				writeIndex++;
//			}
//			if (value.length > 0) {
//				index = (--writeIndex) << 1;
//				writeLength += value.length - 1;
//			} else {
//				index = writeIndex << 1;
//			}
//			buf[index++] = IFiller.UTF16_BRACKET_R[0];
//			buf[index] = IFiller.UTF16_BRACKET_R[1];
//		}
		return writeLength;
	}
}