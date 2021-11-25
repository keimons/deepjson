package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.compiler.FieldInfo;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.MethodHandleUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MethodHandle}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class InlineCodec extends ExtendedCodec {

	private List<MethodHandle> builds = new ArrayList<MethodHandle>();

	private List<MethodHandle> writers = new ArrayList<MethodHandle>();

	private Map<Object, MethodHandle> readers = new HashMap<Object, MethodHandle>();

	@Override
	public void init(Class<?> clazz) {
		acceptInstantiation(clazz);
		MethodHandles.Lookup lookup = MethodHandleUtil.Lookup();
		List<FieldInfo> fields = new ArrayList<FieldInfo>();
		for (Field field : ClassUtil.getFields(clazz)) {
			fields.add(new FieldInfo(field));
		}
		try {
			for (FieldInfo info : fields) {
				Class<?> type = info.getFieldType();
				MethodHandle writer;
				if (type.isPrimitive() || type == String.class) {
					MethodType mt = MethodType.methodType(
							boolean.class,
							WriterBuffer.class, long.class, char.class, char[].class, type
					);
					writer = lookup.findStatic(CodecUtil.class, "write", mt);
					// 插入一个新的参数在参数列表的最后
					writer = MethodHandles.dropArguments(writer, 5, clazz);
					MethodHandle getter = lookup.unreflectGetter(info.getField()).asType(MethodType.methodType(type, clazz));
					// 折叠getter方法
					writer = MethodHandles.foldArguments(writer, 4, getter);
					// 调整成固定格式
					writer = MethodHandles.dropArguments(writer, 0, WriterContext.class);
				} else {
					builds.add(lookup.unreflectGetter(info.getField()));
					MethodType mt = MethodType.methodType(
							boolean.class,
							WriterContext.class, WriterBuffer.class, long.class, char.class, char[].class
					);
					writer = lookup.findStatic(CodecUtil.class, "write", mt);
					// 调整成固定格式
					writer = MethodHandles.dropArguments(writer, 5, clazz);
				}
				// 绑定参数
				char[] name = info.getWriteName().toCharArray();
				writer = MethodHandles.insertArguments(writer, 4, (Object) name);
				writers.add(writer);

				MethodHandle setter = lookup.unreflectSetter(info.getField()).asType(MethodType.methodType(void.class, clazz, type));
				MethodHandle reader = setter;
				if (type.isPrimitive()) {
					reader = MethodHandles.dropArguments(reader, 2, ReaderBuffer.class);
					reader = MethodHandles.foldArguments(reader, 1, read(type));
					reader = MethodHandles.dropArguments(reader, 2, ReaderContext.class);
					reader = MethodHandles.dropArguments(reader, 3, long.class);
				} else {
					MethodType mt = MethodType.methodType(
							void.class,
							Object.class, ReaderBuffer.class, ReaderContext.class, long.class, Type.class, MethodHandle.class
					);
					reader = lookup.findStatic(CodecUtil.class, "read", mt);
					reader = MethodHandles.insertArguments(reader, 4, info.getField().getGenericType());
					reader = MethodHandles.insertArguments(reader, 4, setter);
				}
				readers.put(new CharArrayNode(name), reader);
			}
			MethodType mt = MethodType.methodType(
					void.class,
					Object.class, ReaderBuffer.class, ReaderContext.class, long.class
			);
			MethodHandle fSetId = lookup.findStatic(CodecUtil.class, "read", mt);
			readers.put(new CharArrayNode(FIELD_SET_ID), fSetId);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void build(WriterContext context, Object value) {
		try {
			for (MethodHandle build : builds) {
				context.build(build.invoke(value));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(WriterContext context, WriterBuffer buf, CodecModel model, Object value, int uniqueId, long options) {
		try {
			char mark = '{';
			if (uniqueId >= 0) {
				buf.writeValue(mark, FIELD_SET_ID, uniqueId);
				mark = ',';
			}
			if (CodecConfig.WHITE_OBJECT.contains(value.getClass())) {
				buf.writeValue(mark, TYPE, value.getClass().getName());
				mark = ',';
			}
			for (MethodHandle writer : writers) {
				if ((boolean) writer.invoke(context, buf, options, mark, value)) {
					mark = ',';
				}
			}
			buf.writeMark('}');
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object decode0(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		Object instance = newInstance(clazz);
		SyntaxToken token = null;
		try {
			for (; ; ) {
				if (token == SyntaxToken.RBRACE) {
					break;
				}
				buf.assertExpectedSyntax(SyntaxToken.STRING);
				MethodHandle reader = readers.get(buf);
				buf.nextToken();
				buf.assertExpectedSyntax(SyntaxToken.COLON);
				buf.nextToken();
				if (reader == null) {
					// 跳过一个对象，这个对象没有引用
					context.decode(buf, Object.class, options);
				} else {
					reader.invoke(instance, buf, context, options);
				}
				token = buf.nextToken();
				if (token == SyntaxToken.RBRACE) {
					break;
				}
				token = buf.nextToken();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return instance;
	}

	private static MethodHandle read(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
		return MethodHandleUtil.Lookup().findVirtual(
				ReaderBuffer.class, clazz.getSimpleName() + "Value", MethodType.methodType(clazz)
		);
	}
}