package com.keimons.deepjson.serializer;

import com.keimons.deepjson.SerializerOptions;
import com.keimons.deepjson.util.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 源代码工厂
 * <p>
 * 根据类中的字段，生成类的序列化工具。
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class SourceCodeFactory {

	private static final List<Class<?>> IMPORT = Arrays.asList(
			ByteBuf.class,
			FieldName.class,
			ISerializer.class,
			SerializerFactory.class,
			SerializerOptions.class,
			UnsafeUtil.class,
			SerializerUtil.class,
			RyuDouble.class,
			RyuFloat.class,
			Unsafe.class
	);

	/**
	 * 构造一个序列化工具类
	 *
	 * @param packageName 生成的包名
	 * @param className   生成的类名
	 * @param clazz       要序列化的类
	 * @return 工具类
	 */
	public static String create(String packageName, String className, Class<?> clazz) {
		List<FieldInfo> fields = new ArrayList<>();
		for (Field field : ClassUtil.getFields(clazz)) {
			fields.add(new FieldInfo(field));
		}
		return create(packageName, className, fields);
	}

	/**
	 * 构造一个序列化工具类型
	 *
	 * @param packageName 包名
	 * @param className   类名
	 * @param fields      字段
	 * @return 工具类
	 */
	private static String create(String packageName, String className, List<FieldInfo> fields) {
		StringBuilder source = new StringBuilder();
		packageClass(source, packageName);
		importClass(source);
		source.append("public class ").append(className).append(" implements ISerializer {\n");
		source.append("\n");
		source.append("\tprivate static final Unsafe unsafe = UnsafeUtil.getUnsafe();\n");
		source.append("\n");

		for (FieldInfo field : fields) {
			source.append("\tprivate final FieldName $")
					.append(field.getField().getName())
					.append(" = new FieldName(\"")
					.append(field.getFieldName().replaceAll("\"", "\\\\\""))
					.append("\");\n")
			;
			source.append("\n");
		}

		source.append("\t@Override\n");
		source.append("\tpublic int length(Object object, long options) {\n");
		source.append("\t\tif (object == null || SerializerOptions.IgnoreNonField.isOptions(options)) {\n");
		source.append("\t\t\treturn 4;\n");
		source.append("\t\t}\n");
		source.append("\t\tint length = 0;\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getField().getType();
			if (type == boolean.class) {
				source.append("\t\tboolean ")
						.append(fieldName)
						.append(" = unsafe.getBoolean(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += (")
						.append(fieldName)
						.append(" ? 4 : 5) + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == byte.class) {
				source.append("\t\tbyte ")
						.append(fieldName)
						.append(" = unsafe.getByte(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == char.class) {
				source.append("\t\tlength += ").append(field.length() + 4).append(";\n");
			} else if (type == short.class) {
				source.append("\t\tshort ")
						.append(fieldName)
						.append(" = unsafe.getShort(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == int.class) {
				source.append("\t\tint ")
						.append(fieldName)
						.append(" = unsafe.getInt(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == long.class) {
				source.append("\t\tlong ")
						.append(fieldName)
						.append(" = unsafe.getLong(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == float.class) {
				source.append("\t\tfloat ")
						.append(fieldName)
						.append(" = unsafe.getFloat(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += RyuFloat.length(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == double.class) {
				source.append("\t\tdouble ")
						.append(fieldName)
						.append(" = unsafe.getDouble(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tlength += RyuDouble.length(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else {
				source.append("\t\tObject ")
						.append(fieldName)
						.append(" = unsafe.getObject(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tif (").append(fieldName).append(" == null) {\n");
				source.append("\t\t\tif (!SerializerOptions.IgnoreNonField.isOptions(options)) {\n");
				source.append("\t\t\t\tlength += ").append(field.length() + 5).append(";\n");
				source.append("\t\t\t}\n");
				source.append("\t\t} else {\n");
				source.append("\t\t\tISerializer serializer = SerializerFactory.getSerializer(")
						.append(fieldName)
						.append(".getClass());\n")
				;
				source.append("\t\t\tlength += serializer.length(")
						.append(fieldName)
						.append(", options) + ")
						.append(field.length() + 1)
						.append(";\n")
				;
				source.append("\t\t}\n");
			}
		}
		source.append("\t\tif (length == 0) {\n");
		source.append("\t\t\tlength++;\n");
		source.append("\t\t}\n");
		source.append("\t\tlength++;\n");
		source.append("\t\treturn length;\n");
		source.append("\t}\n");
		source.append("\n");

		source.append("\t@Override\n");
		source.append("\tpublic byte coder(Object object, long options) {\n");
		if (fields.stream().anyMatch(field -> field.coder() == SerializerUtil.UTF16)) {
			source.append("\t\treturn 1;\n");
		} else {
			source.append("\t\tif (object == null) {\n");
			source.append("\t\t\treturn 0;\n");
			source.append("\t\t}\n");
			source.append("\t\tbyte coder = 0;\n");
			for (int i = 0; i < fields.size(); i++) {
				String fieldName = "value" + i;
				FieldInfo field = fields.get(i);
				Class<?> type = field.getField().getType();
				if (type == char.class) {
					source.append("\t\tchar ")
							.append(fieldName)
							.append(" = unsafe.getChar(object, ")
							.append(field.offset())
							.append("L);\n")
					;
					source.append("\t\tif (").append(fieldName).append(" >>> 8 != 0) {\n");
					source.append("\t\t\treturn 1;\n");
					source.append("\t\t}\n");
				}
				if (!type.isPrimitive()) {
					source.append("\t\tObject ")
							.append(fieldName)
							.append(" = unsafe.getObject(object, ")
							.append(field.offset())
							.append("L);\n")
					;
					source.append("\t\tif (").append(fieldName).append(" != null) {\n");

					source.append("\t\t\tcoder = SerializerFactory.getSerializer(")
							.append(fieldName)
							.append(".getClass()).coder(")
							.append(fieldName)
							.append(", options);\n");
					source.append("\t\t\tif (coder == 1) {\n");
					source.append("\t\t\t\treturn 1;\n");
					source.append("\t\t\t}\n");
					source.append("\t\t}\n");
				}
			}
			source.append("\t\treturn 0;\n");
		}
		source.append("\t}\n");
		source.append("\n");

		source.append("\t@Override\n");
		source.append("\tpublic void write(Object object, ByteBuf buf) {\n");
		source.append("\t\tif (object == null) {\n");
		source.append("\t\t\tbuf.writeNull();\n");
		source.append("\t\t\treturn;\n");
		source.append("\t\t}\n");
		source.append("\t\tbyte mark = '{';\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getField().getType();
			if (type == boolean.class) {
				source.append("\t\tboolean ")
						.append(fieldName)
						.append(" = unsafe.getBoolean(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == byte.class) {
				source.append("\t\tbyte ")
						.append(fieldName)
						.append(" = unsafe.getByte(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == char.class) {
				source.append("\t\tchar ")
						.append(fieldName)
						.append(" = unsafe.getChar(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == short.class) {
				source.append("\t\tshort ")
						.append(fieldName)
						.append(" = unsafe.getShort(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == int.class) {
				source.append("\t\tint ")
						.append(fieldName)
						.append(" = unsafe.getInt(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == long.class) {
				source.append("\t\tlong ")
						.append(fieldName)
						.append(" = unsafe.getLong(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == float.class) {
				source.append("\t\tfloat ");
				source.append(fieldName)
						.append(" = unsafe.getFloat(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == double.class) {
				source.append("\t\tdouble ")
						.append(fieldName)
						.append(" = unsafe.getDouble(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else {
				source.append("\t\tObject ")
						.append(fieldName)
						.append(" = unsafe.getObject(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			}
			source.append("\t\tbuf.writeValue(mark, $")
					.append(field.getField().getName())
					.append(", ")
					.append(fieldName)
					.append(");\n")
			;
			source.append("\t\tmark = ',';\n");
		}
		source.append("\t\tbuf.writeEndObject();\n");
		source.append("\t}\n");
		source.append("}");
		return source.toString();
	}

	private static void packageClass(StringBuilder source, String packageName) {
		source.append("package ").append(packageName).append(";\n").append("\n");
	}

	/**
	 * 引用所有文件头
	 *
	 * @param source Java源代码
	 */
	private static void importClass(StringBuilder source) {
		for (Class<?> clazz : IMPORT) {
			source.append("import ").append(clazz.getName()).append(";\n");
		}
		source.append("\n");
	}

	private static void writeGetValue(StringBuilder sb, String fieldName, String type, long offset) {
		sb.append("\t\t\tbuf.writeValue(mark, ")
				.append(fieldName)
				.append("$UTF16")
				.append(", ")
				.append("unsafe.get")
				.append(type)
				.append("(object, ")
				.append(offset)
				.append("L));\n")
		;
		sb.append("\t\t\tmark = ',';\n");
	}
}