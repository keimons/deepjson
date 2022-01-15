package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.compiler.Property;
import com.keimons.deepjson.internal.util.CodecUtils;
import com.keimons.deepjson.internal.util.LookupUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MethodHandle}实现无参构造方法实体编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class ParameterlessEntityCodec extends AbstractEntityCodec {

	private final Map<JsonReader.Buffer, MethodHandle> decoders = new HashMap<JsonReader.Buffer, MethodHandle>();

	@Override
	protected void initConstructor() throws Exception {
		constructor = LookupUtils.lookup().findConstructor(clazz, MethodType.methodType(void.class));
	}

	@Override
	public void initDecoder(List<Property> properties) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = LookupUtils.lookup();
		for (Property property : properties) {
			Class<?> type = property.getFieldType();
			MethodType mt = MethodType.methodType(void.class, clazz, type);
			MethodHandle setter = lookup.unreflectSetter(property.getField()).asType(mt);
			MethodHandle decoder;
			if (type.isPrimitive()) {
				decoder = generateReadPrimitiveHandle(property, setter);
			} else {
				decoder = generateReadObjectHandle(property, setter);
			}
			decoders.put(new JsonReader.Buffer(property.getWriteName().toCharArray()), decoder);
		}
		MethodHandle decoder = generateReadSetIdHandle();
		decoders.put(new JsonReader.Buffer(FIELD_SET_ID), decoder);
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
			MethodHandle decoder = decoders.get(buf.buffer());
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.COLON);
			buf.nextToken();
			if (decoder == null) {
				// 跳过一个对象，这个对象没有引用
				context.decode(buf, Object.class, options);
			} else {
				decoder.invoke(instance, context, buf, options);
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			token = buf.nextToken();
		}
		return instance;
	}

	private MethodHandle generateReadPrimitiveHandle(Property property, MethodHandle setter) throws NoSuchMethodException, IllegalAccessException {
		MethodHandle reader = generateReadPrimitiveHandle(property);
		setter = MethodHandles.dropArguments(setter, 2, ReaderContext.class, JsonReader.class, long.class);
		return MethodHandles.foldArguments(setter, 1, reader);
	}

	private MethodHandle generateReadSetIdHandle() throws NoSuchMethodException, IllegalAccessException {
		MethodType mt = MethodType.methodType(void.class,
				Object.class, ReaderContext.class, JsonReader.class, long.class
		);
		return LookupUtils.lookup().findStatic(CodecUtils.class, "readSetId", mt);
	}

	private MethodHandle generateReadObjectHandle(Property property, MethodHandle setter) throws NoSuchMethodException, IllegalAccessException {
		MethodType mt = MethodType.methodType(void.class,
				Object.class, ReaderContext.class, JsonReader.class, long.class, Type.class, MethodHandle.class
		);
		Type genericType = property.getField().getGenericType();
		MethodHandle mh = LookupUtils.lookup().findStatic(CodecUtils.class, "readObject", mt);
		return MethodHandles.insertArguments(mh, 4, genericType, setter);
	}
}