package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.compiler.Property;
import com.keimons.deepjson.internal.util.FieldUtils;
import com.keimons.deepjson.internal.util.LookupUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
public class DefaultEntityCodec extends ExtendedCodec {

	private List<BuildNode> builds = new ArrayList<BuildNode>();

	private List<MethodHandle> writers = new ArrayList<MethodHandle>();

	private Map<JsonReader.Buffer, MethodHandle> readers = new HashMap<JsonReader.Buffer, MethodHandle>();

	@Override
	public void init(Class<?> clazz) {
		acceptInstantiation(clazz);
		MethodHandles.Lookup lookup = LookupUtil.lookup();
		List<Property> fields = FieldUtils.createProperties(clazz);
		try {
			for (Property info : fields) {
				Class<?> type = info.getFieldType();
				MethodHandle writer;
				if (type.isPrimitive() || type == String.class) {
					MethodType mt = MethodType.methodType(
							boolean.class,
							JsonWriter.class, long.class, char.class, char[].class, type
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
					// 更加保守的保守策略
					if (TypedCollectionCodec.test(info.getField())) {
						@SuppressWarnings({"rawtypes", "unchecked"})
						ICodec<Object> codec = new TypedCollectionCodec(info.getField());
						builds.add(new BuildNode(lookup.unreflectGetter(info.getField()), codec));
					} else if (TypedMapCodec.test(info.getField())) {
						@SuppressWarnings({"rawtypes", "unchecked"})
						ICodec<Object> codec = new TypedMapCodec(info.getField());
						builds.add(new BuildNode(lookup.unreflectGetter(info.getField()), codec));
					} else {
						builds.add(new BuildNode(lookup.unreflectGetter(info.getField())));
					}
					MethodType mt = MethodType.methodType(
							boolean.class,
							WriterContext.class, JsonWriter.class, long.class, char.class, char[].class
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
					reader = MethodHandles.dropArguments(reader, 2, JsonReader.class);
					reader = MethodHandles.foldArguments(reader, 1, makePrimitiveDecode(type));
					reader = MethodHandles.dropArguments(reader, 2, ReaderContext.class);
					reader = MethodHandles.dropArguments(reader, 3, long.class);
				} else {
					MethodType mt = MethodType.methodType(
							void.class,
							Object.class, JsonReader.class, ReaderContext.class, long.class, Type.class, MethodHandle.class
					);
					reader = lookup.findStatic(CodecUtil.class, "read", mt);
					reader = MethodHandles.insertArguments(reader, 4, info.getField().getGenericType());
					reader = MethodHandles.insertArguments(reader, 4, setter);
				}
				readers.put(new JsonReader.Buffer(name), reader);
			}
			MethodType mt = MethodType.methodType(
					void.class,
					Object.class, JsonReader.class, ReaderContext.class, long.class
			);
			MethodHandle fSetId = lookup.findStatic(CodecUtil.class, "read", mt);
			readers.put(new JsonReader.Buffer(FIELD_SET_ID), fSetId);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void build(WriterContext context, Object value) {
		try {
			for (BuildNode node : builds) {
				// 对象中取值
				Object item = node.handle.invoke(value);
				if (item == null || !node.typed) {
					context.build(item);
				} else {
					context.build(node.handle.invoke(value), node.codec);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Object value, int uniqueId, long options) {
		try {
			char mark = '{';
			if (uniqueId >= 0) {
				writer.writeValue(mark, FIELD_SET_ID, uniqueId);
				mark = ',';
			}
			if (CodecConfig.WHITE_OBJECT.contains(value.getClass())) {
				writer.writeValue(mark, TYPE, value.getClass().getName());
				mark = ',';
			}
			for (MethodHandle handle : writers) {
				if ((boolean) handle.invoke(context, writer, options, mark, value)) {
					mark = ',';
				}
			}
			if (mark == '{') {
				writer.writeMark('{');
			}
			writer.writeMark('}');
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object decode0(ReaderContext context, JsonReader buf, Class<?> clazz, long options) throws Throwable {
		Object instance = newInstance(clazz);
		SyntaxToken token = buf.token();
		for (; ; ) {
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.STRING);
			MethodHandle reader = readers.get(buf.buffer());
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
		return instance;
	}

	/**
	 * 生成一个基础类型的读取句柄
	 *
	 * @param clazz 要生成读取句柄的类型，仅包括基础类型
	 * @return 基础类型的读取句柄
	 * @throws NoSuchMethodException  方法查找失败
	 * @throws IllegalAccessException 非法资源访问
	 * @see JsonReader 读取器
	 */
	protected static MethodHandle makePrimitiveDecode(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException {
		if (!clazz.isPrimitive() || clazz == void.class) {
			throw new IllegalAccessException("cannot make reader of " + clazz);
		}
		return LookupUtil.lookup().findVirtual(
				JsonReader.class, clazz.getSimpleName() + "Value", MethodType.methodType(clazz)
		);
	}

	/**
	 * 构造节点
	 */
	private static class BuildNode {

		boolean typed;

		MethodHandle handle;

		ICodec<Object> codec;

		public BuildNode(MethodHandle handle) {
			this.handle = handle;
			this.typed = false;
		}

		public BuildNode(MethodHandle handle, ICodec<Object> codec) {
			this.handle = handle;
			this.codec = codec;
			this.typed = true;
		}
	}
}