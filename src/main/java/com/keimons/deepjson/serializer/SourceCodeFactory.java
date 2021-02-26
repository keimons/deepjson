package com.keimons.deepjson.serializer;

import com.keimons.deepjson.filler.FieldInfo;
import com.keimons.deepjson.util.ClassUtil;

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
		StringBuilder sb = new StringBuilder();
		sb.append("package ").append(packageName).append(";\n");
		sb.append("\n");
		sb.append("import com.keimons.deepjson.serializer.ISerializer;\n");
		sb.append("import com.keimons.deepjson.serializer.ByteBuf;\n");
		sb.append("import com.keimons.deepjson.SerializerOptions;\n");
		sb.append("import com.keimons.deepjson.util.UnsafeUtil;\n");
		sb.append("import com.keimons.deepjson.filler.SerializerUtil;\n");
		sb.append("import sun.misc.Unsafe;\n");
		sb.append("\n");
		sb.append("import java.text.DecimalFormat;\n");
		sb.append("\n");
		sb.append("public class ").append(className).append(" implements ISerializer {\n");
		sb.append("\n");
		sb.append("\tprivate static final Unsafe unsafe = UnsafeUtil.getUnsafe();\n");
		sb.append("\n");

		for (FieldInfo field : fields) {
			String latin = Arrays.toString(field.getFieldNameByLatin());
			String utf16 = Arrays.toString(field.getFieldNameByUtf16());
			sb.append("\tprivate final byte[] ")
					.append(field.getField().getName())
					.append("$LATIN")
					.append(" = {")
					.append(latin, 1, latin.length() - 1)
					.append("};\n")
			;
			sb.append("\tprivate final byte[] ")
					.append(field.getField().getName())
					.append("$UTF16")
					.append(" = {")
					.append(utf16, 1, utf16.length() - 1)
					.append("};\n")
			;
			sb.append("\n");
		}

		sb.append("\tprivate final DecimalFormat format = new DecimalFormat();\n");
		sb.append("\n");

		sb.append("\t@Override\n");
		sb.append("\tpublic int length(Object object, long options) {\n");
		sb.append("\t\tif (object == null || SerializerOptions.IgnoreNonField.isOptions(options)) {\n");
		sb.append("\t\t\treturn 4;\n");
		sb.append("\t\t}\n");
		sb.append("\t\tint length = 0;\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getField().getType();
			if (type == boolean.class) {
				sb.append("\t\tboolean ")
						.append(fieldName)
						.append(" = unsafe.getBoolean(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += (")
						.append(fieldName)
						.append(" ? 4 : 5) + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == byte.class) {
				sb.append("\t\tbyte ")
						.append(fieldName)
						.append(" = unsafe.getByte(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == char.class) {
				sb.append("\t\tlength += ").append(field.length() + 3).append(";\n");
			} else if (type == short.class) {
				sb.append("\t\tshort ")
						.append(fieldName)
						.append(" = unsafe.getShort(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == int.class) {
				sb.append("\t\tint ")
						.append(fieldName)
						.append(" = unsafe.getInt(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == long.class) {
				sb.append("\t\tlong ")
						.append(fieldName)
						.append(" = unsafe.getLong(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += SerializerUtil.size(")
						.append(fieldName)
						.append(") + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == float.class) {
				sb.append("\t\tfloat ")
						.append(fieldName)
						.append(" = unsafe.getFloat(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += format.format(")
						.append(fieldName)
						.append(").length() + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			} else if (type == double.class) {
				sb.append("\t\tdouble ")
						.append(fieldName)
						.append(" = unsafe.getDouble(object, ")
						.append(field.offset())
						.append("L);\n")
				;
				sb.append("\t\tlength += format.format(")
						.append(fieldName)
						.append(").length() + ")
						.append(field.length() + 1)
						.append(";\n")
				;
			}
		}
		sb.append("\t\tif (length == 0) {\n");
		sb.append("\t\t\tlength++;\n");
		sb.append("\t\t}\n");
		sb.append("\t\tlength++;\n");
		sb.append("\t\treturn length;\n");
		sb.append("\t}\n");
		sb.append("\n");

		sb.append("\t@Override\n");
		sb.append("\tpublic void write(Object object, ByteBuf buf) {\n");
		sb.append("\t\tif (object == null) {\n");
		sb.append("\t\t\tbuf.writeNull();\n");
		sb.append("\t\t\treturn;\n");
		sb.append("\t\t}\n");
		sb.append("\t\tif (buf.getCoder() == 0) {\n");
		sb.append("\t\t} else {\n");
		sb.append("\t\t\tbyte mark = '{';\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getField().getType();
			if (type == boolean.class) {
				sb.append("\t\t\tboolean ")
						.append(fieldName)
						.append(" = unsafe.getBoolean(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == byte.class) {
				sb.append("\t\t\tbyte ")
						.append(fieldName)
						.append(" = unsafe.getByte(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == char.class) {
				sb.append("\t\t\tchar ")
						.append(fieldName)
						.append(" = unsafe.getChar(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == short.class) {
				sb.append("\t\t\tshort ")
						.append(fieldName)
						.append(" = unsafe.getShort(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == int.class) {
				sb.append("\t\t\tint ")
						.append(fieldName)
						.append(" = unsafe.getInt(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == long.class) {
				sb.append("\t\t\tlong ")
						.append(fieldName)
						.append(" = unsafe.getLong(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == float.class) {
				sb.append("\t\t\tfloat ");
				sb.append(fieldName)
						.append(" = unsafe.getFloat(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			} else if (type == double.class) {
				sb.append("\t\t\tdouble ")
						.append(fieldName)
						.append(" = unsafe.getDouble(object, ")
						.append(field.offset())
						.append("L);\n")
				;
			}
			sb.append("\t\t\tbuf.writeValue(mark, ")
					.append(field.getField().getName())
					.append("$UTF16")
					.append(", ")
					.append(fieldName)
					.append(");\n")
			;
			sb.append("\t\t\tmark = ',';\n");
		}
		sb.append("\t\t}\n");
		sb.append("\t\tbuf.writeEndObject();\n");
		sb.append("\t}\n");
		sb.append("}");
		return sb.toString();
	}
}