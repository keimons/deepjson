package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.*;
import com.keimons.deepjson.annotation.CodecCreator;
import com.keimons.deepjson.compiler.Property;
import com.keimons.deepjson.internal.util.CodecUtils;
import com.keimons.deepjson.internal.util.FieldUtils;
import com.keimons.deepjson.internal.util.LookupUtils;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.InitializeCodecFailedException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 实体类编解码器
 * <p>
 * 实体类的序列化，无论对象有没有无参构造方法，是没有影响的，均使用抽象类中的{@link #encode(WriterContext,
 * JsonWriter, CodecModel, Object, int, long)}方法进行序列化。而对于反序列化，则有区分，出于节省内存的
 * 目标，使用带参数和使用无参数构造方法的实体类，会有两个编解码器：
 * <ul>
 *     <li>{@link ParameterizedEntityCodec}使用有参构造方法解码</li>
 *     <li>{@link ParameterlessEntityCodec}使用无参构造方法解码</li>
 * </ul>
 * 当使用无参构造方法时，可以先{@code new Object()}，然后将对象中的属性一一赋值。而使用有参构造方法时，需要
 * 将对象中的所有属性解析并缓存，直到全部信息集齐，才能使用指定的参数生成对象。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public abstract class AbstractEntityCodec extends ExtendedCodec {

	protected MethodHandle constructor;

	private final List<BuildNode> builders = new ArrayList<BuildNode>();

	private final List<MethodHandle> encoders = new ArrayList<MethodHandle>();

	@Override
	public void init(Class<?> clazz) {
		acceptInstantiation(clazz);
		List<Property> properties = FieldUtils.createProperties(clazz);
		try {
			initConstructor();
			initBuilder(properties);
			initEncoder(properties);
			initDecoder(properties);
		} catch (Exception e) {
			throw new InitializeCodecFailedException(e);
		}
	}

	/**
	 * 初始化构造方法句柄
	 * <p>
	 * 实体类中可能包含或不包含无参构造方法，有以下处理方式：
	 * <ul>
	 *     <li>类中包含无参构造方法，直接使用无参构造方法。</li>
	 *     <li>类中不包含无参构造方法或通过注解指定构造方法，则使用有参构造方法或指定构造方法。</li>
	 * </ul>
	 * 在使用带参数的参构造方法时，构造方法句柄的生成过程大致如下：
	 * <pre>
	 *     public Node(String name, int index) {
	 *         // do something
	 *     }
	 *     mh.invoke("keimons", 0);
	 * </pre>
	 * 插入一个参数{@link Map}用于获取某些属性：
	 * <pre>
	 *     public static syntheticNode(String name, int index, Map properties) {
	 *         // do something
	 *     }
	 *     mh.invoke("keimons", 0, properties);
	 * </pre>
	 * 将构造方法中的参数使用{@link Map#getOrDefault(Object, Object)}方法代替：
	 * <pre>
	 *     public static syntheticNode(Map properties) {
	 *         String name = properties.getOrDefault("name", null);
	 *         int index = properties.getOrDefault("index", 0);
	 *         return new Node(name, index);
	 *     }
	 *     mh.invoke(properties);
	 * </pre>
	 * 通过一系列的转化，将构造方法句柄从传入参数转化为传入{@link Map}并从中取值。
	 *
	 * @throws Exception 初始化构造方法中的异常
	 * @see CodecCreator 指定构造方法
	 */
	protected abstract void initConstructor() throws Exception;

	/**
	 * 初始化属性查找句柄
	 * <p>
	 * 对于类中的属性，并不是所有属性都参与到循环检测中，有些属性是不参与的：
	 * <ul>
	 *     <li>{@link String}属性和{@code primitive}属性，直接跳过。</li>
	 *     <li>{@link Object}属性，调用对象的{@code getter}句柄查找。</li>
	 * </ul>
	 * 如果已知这个属性是{@link Collection}或{@link Map}，并且内部元素均为已知的终极元素，
	 * 则可以进一步优化为{@link TypedCollectionCodec}和{@link TypedMapCodec}。
	 *
	 * @param properties 类中所有属性
	 * @throws IllegalAccessException 访问权限异常
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	protected void initBuilder(List<Property> properties) throws IllegalAccessException {
		MethodHandles.Lookup lookup = LookupUtils.lookup();
		for (Property property : properties) {
			Class<?> type = property.getFieldType();
			// 基础类型和String类型不参与
			if (type.isPrimitive() || type == String.class) {
				continue;
			}
			// 更加保守的保守策略
			if (TypedCollectionCodec.test(property.getField())) {
				ICodec<Object> codec = new TypedCollectionCodec(property.getField());
				builders.add(new BuildNode(lookup.unreflectGetter(property.getField()), codec));
			} else if (TypedMapCodec.test(property.getField())) {
				ICodec<Object> codec = new TypedMapCodec(property.getField());
				builders.add(new BuildNode(lookup.unreflectGetter(property.getField()), codec));
			} else {
				builders.add(new BuildNode(lookup.unreflectGetter(property.getField())));
			}
		}
	}

	/**
	 * 初始化编码器句柄
	 * <p>
	 * 将属性写入到缓冲区中时，需要将对象传入方法中，通过对象的getter方法获取对象属性，
	 * 再将属性传入到真正的编码器中。该过程大致如下：
	 * <p>
	 * 针对于方法：
	 * <pre>
	 *     public static void write(int value) {
	 *         // do something
	 *     }
	 *     mh.invoke(x);
	 * </pre>
	 * 首先转化方法句柄，添加一个参数：
	 * <pre>
	 *     public static void syntheticWrite0(int value, Node node) {
	 *         write(value);
	 *     }
	 *     mh.invoke(x, node);
	 * </pre>
	 * 将{@code value}参数使用{@code Node}对象的{@code getter}方法替换：
	 * <pre>
	 *     public static void syntheticWrite1(Node node) {
	 *         int value = node.getValue();
	 *         write(value);
	 *     }
	 *     mh.invoke(node);
	 * </pre>
	 * 方法句柄转化后，对于原来句柄{@code (I)V}的调用，可以使用新句柄{@code (Lcom.x.Node;)V}完成。
	 *
	 * @param properties 类中所有属性
	 * @throws NoSuchMethodException  方法查找异常
	 * @throws IllegalAccessException 访问权限异常
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], boolean)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], char)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], byte)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], short)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], int)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], long)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], float)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], double)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], String)
	 * @see CodecUtils#write(WriterContext, JsonWriter, long, char, char[], Object)
	 */
	protected void initEncoder(List<Property> properties) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = LookupUtils.lookup();
		for (Property property : properties) {
			Class<?> type = property.getFieldType();
			Class<?> paramType = type.isPrimitive() || type == String.class ? type : Object.class;
			MethodHandle getter = lookup.unreflectGetter(property.getField()).asType(MethodType.methodType(paramType, clazz));
			MethodType mt = MethodType.methodType(
					boolean.class,
					WriterContext.class, JsonWriter.class, long.class, char.class, char[].class, paramType
			);
			MethodHandle encoder = lookup.findStatic(CodecUtils.class, "write", mt);
			// 处理最后一个参数，该参数调用对象的getter方法，从对象中获取
			encoder = MethodHandles.dropArguments(encoder, 6, clazz);
			encoder = MethodHandles.foldArguments(encoder, 5, getter);
			Object name = property.getWriteName().toCharArray();
			encoder = MethodHandles.insertArguments(encoder, 4, name);
			encoders.add(encoder);
		}
	}

	/**
	 * 初始化解码器句柄
	 *
	 * @param properties 属性
	 * @throws NoSuchMethodException  方法查找异常
	 * @throws IllegalAccessException 权限访问异常
	 */
	protected abstract void initDecoder(List<Property> properties) throws NoSuchMethodException, IllegalAccessException;

	@Override
	public void build(WriterContext context, Object value) {
		try {
			for (BuildNode node : builders) {
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
			for (MethodHandle encode : encoders) {
				if ((boolean) encode.invoke(context, writer, options, mark, value)) {
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
	protected abstract Object decode0(ReaderContext context, JsonReader buf, Class<?> clazz, long options) throws Throwable;

	/**
	 * 根据属性信息生成读取缓冲区的句柄
	 * <p>
	 * 在缓冲区中读取信息时，根据属性的不同，使用不同的读取句柄。转化后的句柄为固定格式：
	 * {@code (Lcom.keimons.deepjson.ReaderContext;Lcom.keimons.deepjson.JsonReader;J)T}，
	 * 其中返回类型{@code T}由属性类型决定：
	 * <ul>
	 *     <li>基础类型，返回基础类型</li>
	 *     <li>其它类型，返回{@link Object}类型</li>
	 * </ul>
	 * <p>
	 * 该转化过程过程大致如下：
	 * <p>
	 * 针对于方法：
	 * <pre>
	 *     public Object readObject(ReaderContext context, JsonReader reader, long options, Type type) {
	 *         return context.decode(reader, type, options);
	 *     }
	 *     mh.invoke(context, reader, options, Node.class);
	 * </pre>
	 * 将{@code Type}绑定为属性的类型：
	 * <pre>
	 *     public Object syntheticReadObject(ReaderContext context, JsonReader reader, long options) {
	 *         Type type = Node.class;
	 *         return readObject(context, reader, options, type);
	 *     }
	 *     mh.invoke(context, reader, options);
	 * </pre>
	 * 对于基础类型，{@code Type}的绑定是没有意义的，因为基础类型是固定的使用{@link JsonReader}中针对于基础类型的读取方法。
	 * 对于其他类型，则应该在缓冲区中继续进行对象解析。
	 *
	 * @param property 属性信息
	 * @return 属性的读取缓冲区句柄
	 * @throws NoSuchMethodException  方法查找失败
	 * @throws IllegalAccessException 非法资源访问
	 * @see CodecUtils#readBoolean(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readCharacter(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readByte(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readShort(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readInteger(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readLong(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readFloat(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readDouble(ReaderContext, JsonReader, long, Type)
	 * @see CodecUtils#readObject(ReaderContext, JsonReader, long, Type)
	 */
	protected MethodHandle generateReadPrimitiveHandle(Property property) throws NoSuchMethodException, IllegalAccessException {
		Class<?> type = property.getFieldType();
		Type genericType = property.getField().getGenericType();
		Class<?> pt = type.isPrimitive() ? type : Object.class;

		MethodType mt = MethodType.methodType(pt, ReaderContext.class, JsonReader.class, long.class, Type.class);
		// generate method name of reader
		String mn = "read" + ClassUtil.findWrapperClass(pt).getSimpleName();
		MethodHandle reader = LookupUtils.lookup().findStatic(CodecUtils.class, mn, mt);
		return MethodHandles.insertArguments(reader, 3, genericType);
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