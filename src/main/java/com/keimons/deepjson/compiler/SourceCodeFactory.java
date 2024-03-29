package com.keimons.deepjson.compiler;

import com.keimons.deepjson.internal.util.FieldUtils;
import com.keimons.deepjson.util.ArrayUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	/**
	 * 构造一个编解码工具类
	 *
	 * @param packageName 生成的包名
	 * @param className   生成的类名
	 * @param clazz       要编解码的类
	 * @return 工具类
	 */
	public static String create(String packageName, String className, Class<?> clazz) {
		return create(packageName, className, FieldUtils.createProperties(clazz));
	}

	/**
	 * 构造一个编解码工具类型
	 *
	 * @param packageName 包名
	 * @param className   类名
	 * @param fields      字段
	 * @return 工具类
	 */
	private static String create(String packageName, String className, List<Property> fields) {
		StringBuilder source = new StringBuilder();
		packageClass(source, packageName);
		importClass(source);
		source.append("public class ").append(className).append(" extends ExtendedCodec {\n");
		source.append("\n");

		for (Property field : fields) {
			source.append("\tprivate final char[] $")
					.append(field.getField().getName())
					.append(" = \"")
					.append(field.getWriteName())
					.append("\".toCharArray();\n")
			;
			source.append("\n");
		}

		for (Property field : fields) {
			source.append("\tprivate Field $field$_")
					.append(field.getFieldName())
					.append(";\n")
			;
			source.append("\n");
		}

		source.append("\t@Override\n");
		source.append("\tpublic void init(Class<?> clazz) {\n");
		source.append("\t\tacceptInstantiation(clazz);\n");
		for (Property field : fields) {
			source.append("\t\t$field$_")
					.append(field.getFieldName())
					.append(" = findField(clazz, \"")
					.append(field.getFieldName())
					.append("\", ")
					.append(Modifier.isPublic(field.getField().getModifiers()))
					.append(");\n");
			;
		}
		source.append("\t}\n");
		source.append("\n");

		source.append("\t@Override\n");
		source.append("\tpublic void build(WriterContext context, Object value) {\n");
		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			Property field = fields.get(i);
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
		source.append("\tpublic void encode(WriterContext context, JsonWriter writer, CodecModel model, Object object, int uniqueId, long options) throws IOException {\n");
		source.append("\t\tchar mark = '{';\n");
		source.append("\t\tif (uniqueId >= 0) {\n");
		source.append("\t\t\twriter.writeValue(mark, FIELD_SET_ID, uniqueId);\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
		source.append("\t\tif (CodecConfig.WHITE_OBJECT.contains(object.getClass())) {\n");
		source.append("\t\t\twriter.writeValue(mark, TYPE, object.getClass().getName());\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");

		for (int i = 0; i < fields.size(); i++) {
			String fieldName = "value" + i;
			Property field = fields.get(i);
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
		source.append("\t\t\twriter.writeMark('{');\n");
		source.append("\t\t}\n");
		source.append("\t\twriter.writeMark('}');\n");
		source.append("\t}\n");

		TreeMap<Integer, ArrayList<Property>> map = makeCase(fields);

		source.append("\n");
		source.append("\t@Override\n");
		source.append("\tpublic Object decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {\n");
		source.append("\t\tObject instance = newInstance(clazz);\n");
		source.append("\t\tSyntaxToken token = null;\n");
		source.append("\t\tfor (; ; ) {\n");
		source.append("\t\t\tif (token == SyntaxToken.RBRACE) {\n");
		source.append("\t\t\t\tbreak;\n");
		source.append("\t\t\t}\n");
		source.append("\t\t\treader.assertExpectedSyntax(SyntaxToken.STRING);\n");
		source.append("\t\t\tint index = switch0(reader);\n");
		source.append("\t\t\treader.nextToken();\n");
		source.append("\t\t\treader.assertExpectedSyntax(SyntaxToken.COLON);\n");
		source.append("\t\t\ttoken = reader.nextToken();\n");
		source.append("\t\t\tswitch (index) {\n");

		source.append("\t\t\t\tcase 0: {\n");
		source.append("\t\t\t\t\tcontext.put(reader.intValue(), instance);\n");
		source.append("\t\t\t\t}\n");
		source.append("\t\t\t\tbreak;\n");

		int count = 1;
		for (Map.Entry<Integer, ArrayList<Property>> entry : map.entrySet()) {
			for (Property info : entry.getValue()) {
				appendCase(source, count++, info);
			}
		}
		source.append("\t\t\t\tdefault:\n");
		source.append("\t\t\t\t\tcontext.decode(reader, Object.class, options);\n");
		source.append("\t\t\t}\n");
		source.append("\t\t\ttoken = reader.nextToken();\n");
		source.append("\t\t\tif (token == SyntaxToken.RBRACE) {\n");
		source.append("\t\t\t\tbreak;\n");
		source.append("\t\t\t}\n");
		source.append("\t\t\ttoken = reader.nextToken();\n");
		source.append("\t\t}\n");
		source.append("\t\treturn instance;\n");
		source.append("\t}\n");
		source.append("\n");

		appendSwitch(source, map);
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
		source.append("import com.keimons.deepjson.*;\n");
		source.append("\n");
		source.append("import java.io.IOException;\n");
		source.append("import java.lang.reflect.Field;\n");
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

		source.append("\t\twriter.writeValue(mark, $")
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
		source.append("\t\t\twriter.writeValue(mark, $")
				.append(ref)
				.append(", ")
				.append(name)
				.append(");\n")
		;
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
	}

	public static void appendObject(StringBuilder source, String ref) {
		source.append("\t\tif (context.isEmptyHead() && CodecOptions.IgnoreNonField.isOptions(options)) {\n");
		source.append("\t\t\tcontext.poll();\n");
		source.append("\t\t} else {\n");
		source.append("\t\t\twriter.writeName(mark, $").append(ref).append(");\n");
		source.append("\t\t\tcontext.encode(writer, CodecModel.V, options);\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
	}

	public static TreeMap<Integer, ArrayList<Property>> makeCase(List<Property> fields) {
		TreeMap<Integer, ArrayList<Property>> map = new TreeMap<Integer, ArrayList<Property>>();
		for (Property field : fields) {
			int hashcode = ArrayUtil.hashcode(field.getWriteName().toCharArray());
			ArrayList<Property> list = map.get(hashcode);
			if (list == null) {
				list = new ArrayList<Property>();
				map.put(hashcode, list);
			}
			list.add(field);
		}
		return map;
	}

	public static void appendCase(StringBuilder source, int index, Property info) {
		source.append("\t\t\t\tcase ").append(index).append(": {\n");
		Class<?> type = info.getFieldType();
		String name = type.getName();
		if (type.isPrimitive()) {
			source.append("\t\t\t\t\t")
					.append(name)
					.append(" value = reader.")
					.append(name)
					.append("Value();\n");
			source.append("\t\t\t\t\tunsafe.put")
					.append(toUpperFirst(type.getName()))
					.append("(instance, ")
					.append(info.offset())
					.append("L, value);\n");
		} else {
			source.append("\t\t\t\t\tif (token == SyntaxToken.NULL) {\n");
			source.append("\t\t\t\t\t\tunsafe.putObject(instance, ")
					.append(info.offset()).append("L, null);\n");
			source.append("\t\t\t\t\t} else if (reader.is$Id()) {\n");
			source.append("\t\t\t\t\t\tcontext.addCompleteHook(instance, ")
					.append(info.offset()).append("L, reader.get$Id());\n");
			source.append("\t\t\t\t\t} else {\n");
			source.append("\t\t\t\t\t\tObject value = context.decode(reader, $field$_")
					.append(info.getFieldName()).append(".getGenericType(), options);\n");
			source.append("\t\t\t\t\t\tunsafe.putObject(instance, ")
					.append(info.offset()).append("L, value);\n");
			source.append("\t\t\t\t\t}\n");
		}
		source.append("\t\t\t\t}\n");
		source.append("\t\t\t\tbreak;\n");
	}

	public static void appendSwitch(StringBuilder source, TreeMap<Integer, ArrayList<Property>> map) {
		source.append("\tprivate int switch0(JsonReader buffer) {\n");
		source.append("\t\tJsonReader.Buffer buf = buffer.buffer();\n");
		source.append("\t\tint hashcode = buf.valueHashcode();\n");
		source.append("\t\tswitch (hashcode) {\n");

		// 0 always @id

		int count = 1;
		for (Map.Entry<Integer, ArrayList<Property>> entry : map.entrySet()) {
			int hashcode = entry.getKey();
			source.append("\t\t\tcase ").append(hashcode).append(": {\n");
			for (Property info : entry.getValue()) {
				source.append("\t\t\t\tif (buf.isSame($").append(info.getWriteName()).append(")) {\n");
				source.append("\t\t\t\t\treturn ").append(count++).append(";\n");
				source.append("\t\t\t\t}\n");
			}
			source.append("\t\t\t}\n");
			source.append("\t\t\tbreak;\n");
		}
		source.append("\t\t\tdefault:\n");
		source.append("\t\t\t\tif (buf.isSame(FIELD_SET_ID)) {\n");
		source.append("\t\t\t\t\treturn 0;\n");
		source.append("\t\t\t\t} else {\n");
		source.append("\t\t\t\t\treturn -1;\n");
		source.append("\t\t\t\t}\n");
		source.append("\t\t}\n");
		source.append("\t\treturn -1;\n");
		source.append("\t}\n");
	}

	/**
	 * 首字母大写
	 *
	 * @param content 文本串
	 * @return 首字母大写后的文本串
	 */
	private static String toUpperFirst(String content) {
		char[] chars = {content.charAt(0)};
		String temp = new String(chars);
		return content.replaceFirst(temp, temp.toUpperCase());
	}
}