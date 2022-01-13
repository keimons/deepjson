package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.compiler.Property;
import com.keimons.deepjson.internal.util.ConstructorUtils;
import com.keimons.deepjson.internal.util.FieldUtils;
import com.keimons.deepjson.internal.util.InternalIgnorableException;
import com.keimons.deepjson.internal.util.LookupUtil;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntityCodec
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class ParamsEntityCodec extends DefaultEntityCodec {

	private final Map<String, MethodHandle> readers = new HashMap<String, MethodHandle>();
	private final Map<String, MethodHandle> setters = new HashMap<String, MethodHandle>();

	private MethodHandle constructor;

	private String[] names;

	@Override
	public void init(Class<?> clazz) {
		super.init(clazz);
		MethodHandles.Lookup lookup = LookupUtil.lookup();
		Constructor<?> con = ConstructorUtils.findConstructor(clazz);
		try {
			names = ConstructorUtils.getConstructorParameterNames(con);
			Class<?>[] types = con.getParameterTypes();
			constructor = lookup.unreflectConstructor(con);
			constructor = MethodHandles.dropArguments(constructor, names.length, Map.class);
			MethodHandle mg = lookup.findVirtual(Map.class, "getOrDefault", MethodType.methodType(Object.class, Object.class, Object.class));
			for (int i = names.length - 1; i >= 0; i--) {
				MethodHandle handle = MethodHandles.insertArguments(mg, 1, names[i], ClassUtil.findDefaultValue(types[i]));
				handle = handle.asType(MethodType.methodType(types[i], Map.class));
				constructor = MethodHandles.foldArguments(constructor, i, handle);
			}
		} catch (InternalIgnorableException | IllegalAccessException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		List<Property> properties = FieldUtils.createProperties(clazz);
		try {
			for (Property property : properties) {
				Class<?> type = property.getFieldType();
				MethodHandle reader;
				if (type.isPrimitive()) {
					reader = makePrimitiveDecode(type);
					reader = MethodHandles.dropArguments(reader, 1, ReaderContext.class, long.class);
				} else {
					MethodType mt = MethodType.methodType(Object.class, JsonReader.class, ReaderContext.class, long.class, Type.class);
					reader = lookup.findStatic(CodecUtil.class, "read", mt);
					reader = MethodHandles.insertArguments(reader, 3, property.getField().getGenericType());
				}
				readers.put(property.getFieldName(), reader);
				MethodHandle setter = lookup.unreflectSetter(property.getField());
				setters.put(property.getFieldName(), setter);
			}
			for (String name : names) {
				setters.remove(name);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object decode0(ReaderContext context, JsonReader buf, Class<?> clazz, long options) throws Throwable {
		Map<String, Object> properties = new HashMap<String, Object>();
		SyntaxToken token = buf.token();
		for (; ; ) {
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.STRING);
			String key = buf.stringValue();
			MethodHandle reader = readers.get(key);
			buf.nextToken();
			buf.assertExpectedSyntax(SyntaxToken.COLON);
			buf.nextToken();
			if (reader == null) {
				// 跳过一个对象，这个对象没有引用
				context.decode(buf, Object.class, options);
			} else {
				Object value = reader.invoke(buf, context, options);
				properties.put(key, value);
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACE) {
				break;
			}
			token = buf.nextToken();
		}
		Object instance = constructor.invoke(properties);
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			MethodHandle handle = setters.get(entry.getKey());
			if (handle != null) {
				handle.invoke(instance, entry.getValue());
			}
		}
		return instance;
	}
}