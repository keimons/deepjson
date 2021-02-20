package com.keimons.deepjson.serializer;

import com.keimons.deepjson.filler.FillerFactory;
import com.keimons.deepjson.filler.FillerHelper;
import com.keimons.deepjson.filler.IFiller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ObjectSerializer extends BaseSerializer {

	private List<IFiller> fillers = new ArrayList<>();

	public ObjectSerializer(Class<?> clazz) {
		try {
			for (Field field : clazz.getFields()) {
				try {
					fillers.add(FillerFactory.create(clazz, field));
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			for (Field field : clazz.getDeclaredFields()) {
				try {
					fillers.add(FillerFactory.create(clazz, field));
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int length(Object object) {
		if (object == null) {
			return 0;
		}
		int length = 2;
		for (IFiller filler : fillers) {
			length += filler.length(object);
		}
		return length;
	}

	@Override
	public byte coder(Object object) {
		for (IFiller filler : fillers) {
			byte coder = filler.coder(object);
			if (coder == 1) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public String write(Object object) {
		int length = length(object);
		byte coder = coder(object);
		byte[] buf = new byte[length << coder];
		write(object, buf, coder, 0);
		return newString(buf, coder);
	}

	@Override
	public int write(Object object, byte[] buf, byte coder, int writeIndex) {
		int index = writeIndex << coder;
		if (coder == FillerHelper.LATIN) {
			buf[index] = '{';
		} else {
			buf[index] = UTF16_L[0];
			buf[++index] = UTF16_L[1];
		}
		writeIndex++;
		for (IFiller filler : fillers) {
			writeIndex += filler.concat(object, buf, coder, writeIndex);
		}
		index = writeIndex - 1 << coder;
		if (coder == FillerHelper.LATIN) {
			buf[index] = '}';
		} else {
			buf[index] = UTF16_R[0];
			buf[++index] = UTF16_R[1];
		}
		writeIndex++;
		return writeIndex;
	}

	public void addLast(IFiller filler) {
		this.fillers.add(filler);
	}

	public void removeLast() {
		this.fillers.remove(fillers.size() - 1);
	}

	public List<IFiller> getFillers() {
		return fillers;
	}

	public void setFillers(List<IFiller> fillers) {
		this.fillers = fillers;
	}
}