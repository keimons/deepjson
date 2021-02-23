package com.keimons.deepjson.serializer;

import com.keimons.deepjson.compiler.JdkCompiler;
import com.keimons.deepjson.filler.FillerFactory;
import com.keimons.deepjson.filler.IFiller;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ObjectSerializer extends BaseSerializer {

	ISerializerWriter writer;

	private List<IFiller> fillers = new ArrayList<>();

	public ObjectSerializer(Class<?> clazz) {
		String source = JavacSerializerFactory.create(clazz);
		Class<? extends ISerializerWriter> writerClass = JdkCompiler.compiler(source);
		try {
			writer = writerClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
		try {
			List<Class<?>> classes = new ArrayList<>();
			Class<?> current = clazz;
			do {
				classes.add(current);
				current = current.getSuperclass();
			} while (current != Object.class);

			for (int i = classes.size() - 1; i >= 0; i--) {
				// public default protected private fields
				for (Field field : classes.get(i).getDeclaredFields()) {
					// jump static field
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					// jump transient field
					if (Modifier.isTransient(field.getModifiers())) {
						continue;
					}
					try {
						fillers.add(FillerFactory.create(clazz, field));
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int length(Object object, long options) {
		return writer.length(object, options);
	}

	@Override
	public byte coder(Object object, long options) {
		for (IFiller filler : fillers) {
			byte coder = filler.coder(object, options);
			if (coder == 1) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int write(Object object, ByteBuf buf) {
		writer.write(object, buf);
		return 0;
//		int writeLength = 0;
//		writeLength += buf.writeStartObject();
//		int markIndex = buf.getWriteIndex();
//		for (IFiller filler : fillers) {
//			writeLength += filler.concat(object, buf);
//		}
//		writeLength += buf.writeEndObject(markIndex != buf.getWriteIndex());
//		return writeLength;
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