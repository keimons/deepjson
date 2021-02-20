package com.keimons.deepjson.filler;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.serializer.ISerializer;
import com.keimons.deepjson.serializer.SerializerFactory;

import java.lang.reflect.Field;

public class ObjectFiller extends BaseFiller {

	public ObjectFiller(Class<?> clazz, Field field) {
		super(clazz, field);
	}

	@Override
	public byte coder(Object object, long options) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			return 0;
		}
		ISerializer writer = SerializerFactory.getWriter(value.getClass());
		return (byte) (coder | writer.coder(value, options));
	}

	@Override
	public int length(Object object, long options) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			if (SerializerOptions.IgnoreNonField.isOptions(options)) {
				return size + 4;
			} else {
				return 0;
			}
		}
		ISerializer writer = SerializerFactory.getWriter(value.getClass());
		return size + writer.length(value, options);
	}

	@Override
	public int concat(Object object, byte[] code, byte coder, int writeIndex, long options) {
		Object value = unsafe.getObject(object, offset);
		if (value == null) {
			if (SerializerOptions.IgnoreNonField.isOptions(options)) {
				if (coder == FillerHelper.LATIN) {
					System.arraycopy(value0, 0, code, writeIndex, sizeL);
					writeIndex += sizeL;
					code[writeIndex++] = 'n';
					code[writeIndex++] = 'u';
					code[writeIndex++] = 'l';
					code[writeIndex++] = 'l';
					code[writeIndex] = ',';
				} else {
					System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
					writeIndex += sizeL;
					FillerHelper.putChar2(code, writeIndex++, 'n');
					FillerHelper.putChar2(code, writeIndex++, 'u');
					FillerHelper.putChar2(code, writeIndex++, 'l');
					FillerHelper.putChar2(code, writeIndex++, 'l');
					writeIndex <<= 1;
					code[writeIndex++] = UTF16_SPLIT[0];
					code[writeIndex] = UTF16_SPLIT[1];
				}
				return size + 4;
			} else {
				return 0;
			}
		} else {
			ISerializer writer = SerializerFactory.getWriter(value.getClass());
			if (coder == FillerHelper.LATIN) {
				System.arraycopy(value0, 0, code, writeIndex, sizeL);
				writeIndex += sizeL;
				int writeLength = writer.write(value, code, coder, writeIndex, options);
				code[writeIndex + writeLength] = ',';
				return writeLength + size;
			} else {
				System.arraycopy(value1, 0, code, writeIndex << coder, sizeL << coder);
				writeIndex += sizeL;
				int writeLength = writer.write(value, code, coder, writeIndex, options);
				writeIndex += writeLength;
				writeIndex <<= 1;
				code[writeIndex++] = UTF16_SPLIT[0];
				code[writeIndex] = UTF16_SPLIT[1];
				return writeLength + size;
			}
		}
	}
}