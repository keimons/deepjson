package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.compiler.Property;
import com.keimons.deepjson.internal.util.ConstructorUtils;
import com.keimons.deepjson.internal.util.LookupUtils;
import com.keimons.deepjson.util.ClassUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多参数构造方法
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 9
 **/
public class ParameterizedEntityCodec extends AbstractEntityCodec {

	private String[] parameterNames;

	private final Map<String, MethodHandle> readers = new HashMap<String, MethodHandle>();
	private final Map<String, MethodHandle> setters = new HashMap<String, MethodHandle>();

	@Override
	protected void initConstructor() throws Exception {
		Constructor<?> creator = ConstructorUtils.findConstructor(clazz);
		String[] names = ConstructorUtils.getConstructorParameterNames(creator);
		this.constructor = generateConstructorHandle(creator, names);
		this.parameterNames = names;
	}

	@Override
	protected void initDecoder(List<Property> properties) throws NoSuchMethodException, IllegalAccessException {
		MethodHandles.Lookup lookup = LookupUtils.lookup();
		for (Property property : properties) {
			MethodHandle reader = generateReadPrimitiveHandle(property);
			readers.put(property.getFieldName(), reader);
			MethodHandle setter = lookup.unreflectSetter(property.getField());
			setters.put(property.getFieldName(), setter);
		}
		for (String name : parameterNames) {
			setters.remove(name);
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
				Object value = reader.invoke(context, buf, options);
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

	private MethodHandle generateConstructorHandle(Constructor<?> constructor, String[] names) throws Exception {
		MethodHandles.Lookup lookup = LookupUtils.lookup();
		Class<?>[] types = constructor.getParameterTypes();
		MethodHandle result = lookup.unreflectConstructor(constructor);
		result = MethodHandles.dropArguments(result, names.length, Map.class);
		MethodType mt = MethodType.methodType(Object.class, Object.class, Object.class);
		MethodHandle mg = lookup.findVirtual(Map.class, "getOrDefault", mt);
		for (int i = names.length - 1; i >= 0; i--) {
			MethodHandle handle = MethodHandles.insertArguments(mg, 1, names[i], ClassUtil.findDefaultValue(types[i]));
			handle = handle.asType(MethodType.methodType(types[i], Map.class));
			result = MethodHandles.foldArguments(result, i, handle);
		}
		return result;
	}
}