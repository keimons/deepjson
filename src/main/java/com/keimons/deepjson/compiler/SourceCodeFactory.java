package com.keimons.deepjson.compiler;

import com.keimons.deepjson.AbstractBuffer;
import com.keimons.deepjson.AbstractContext;
import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.Config;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 源代码工厂
 * <p>
 * 根据类中的字段，生成类的编解码工具。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SourceCodeFactory {

	private static final List<Class<?>> IMPORT = new ArrayList<Class<?>>();

	static {
		IMPORT.add(Config.class);
		IMPORT.add(CodecOptions.class);
		IMPORT.add(AbstractBuffer.class);
		IMPORT.add(AbstractContext.class);
		IMPORT.add(UnsafeUtil.class);
		IMPORT.add(Unsafe.class);
	}

	/**
	 * 构造一个编解码工具类
	 *
	 * @param packageName 生成的包名
	 * @param className   生成的类名
	 * @param clazz       要编解码的类
	 * @return 工具类
	 */
	public static String create(String packageName, String className, Class<?> clazz) {
		List<FieldInfo> fields = new ArrayList<FieldInfo>();
		for (Field field : ClassUtil.getFields(clazz)) {
			fields.add(new FieldInfo(field));
		}
		return create(packageName, className, fields);
	}

	/**
	 * 构造一个编解码工具类型
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
		source.append("public class ").append(className).append(" extends ExtendedCodec {\n");
		source.append("\n");

		for (FieldInfo field : fields) {
			source.append("\tprivate final char[] $")
					.append(field.getField().getName())
					.append(" = \"")
					.append(field.getWriteName())
					.append("\".toCharArray();\n")
			;
			source.append("\n");
		}

		source.append("\t@Override\n");
		source.append("\tpublic void build(AbstractContext context, Object value) {\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getFieldType();
			if (!type.isPrimitive() && type != String.class) {
				source.append("\t\tObject ")
						.append(fieldName)
						.append(" = unsafe.getObject(value, ")
						.append(field.offset())
						.append("L);\n")
				;
				source.append("\t\tcontext.build(").append(fieldName).append(");\n");
			}
		}
		source.append("\t}\n");
		source.append("\n");

		source.append("\t@Override\n");
		source.append("\tpublic void encode(AbstractContext context, AbstractBuffer buf, Object object, int uniqueId, long options) {\n");
		source.append("\t\tchar mark = '{';\n");
		source.append("\t\tif (uniqueId >= 0) {\n");
		source.append("\t\t\tbuf.writeValue(mark, FIELD_SET_ID, uniqueId);\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
		source.append("\t\tif (Config.WHITE_OBJECT.contains(object.getClass())) {\n");
		source.append("\t\t\tbuf.writeValue(mark, TYPE, object.getClass().getName());\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");

		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			FieldInfo field = fields.get(i);
			Class<?> type = field.getFieldType();
			if (type.isPrimitive()) {
				appendPrimitive(source, type, fieldName, field.getFieldName(), field.offset());
			} else if (type == String.class) {
				appendString(source, fieldName, field.getFieldName(), field.offset());
			} else {
				appendObject(source, field.getFieldName());
			}
		}
		source.append("\t\tif (mark == '{') {\n");
		source.append("\t\t\tbuf.writeMark('{');\n");
		source.append("\t\t}\n");
		source.append("\t\tbuf.writeMark('}');\n");
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

	public static void appendPrimitive(StringBuilder source, Class<?> clazz, String name, String ref, long offset) {
		source.append("\t\t")
				.append(clazz.getName())
				.append(" ")
				.append(name)
				.append(" = unsafe.get")
				.append(toUpperFirst(clazz.getName()))
				.append("(object, ")
				.append(offset)
				.append("L);\n");

		source.append("\t\tbuf.writeValue(mark, $")
				.append(ref)
				.append(", ")
				.append(name)
				.append(");\n")
		;
		source.append("\t\tmark = ',';\n");
	}

	public static void appendString(StringBuilder source, String name, String ref, long offset) {
		source.append("\t\tString ")
				.append(name)
				.append(" = (String) unsafe.getObject(object, ")
				.append(offset)
				.append("L);\n")
		;
		source.append("\t\tif (")
				.append(name)
				.append(" != null || CodecOptions.IgnoreNonField.noOptions(options)) {\n")
		;
		source.append("\t\t\tbuf.writeValue(mark, $")
				.append(ref)
				.append(", ")
				.append(name)
				.append(");\n")
		;
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
	}

	public static void appendObject(StringBuilder source, String ref) {
		source.append("\t\tif (context.encodeValue(buf, options, mark, $").append(ref).append(")) {\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
	}

	/**
	 * 首字母大写
	 *
	 * @param content 文本串
	 * @return 首字母大写后的文本串
	 */
	private static String toUpperFirst(String content) {
		String temp = new String(new char[]{content.charAt(0)});
		return content.replaceFirst(temp, temp.toUpperCase());
	}
}