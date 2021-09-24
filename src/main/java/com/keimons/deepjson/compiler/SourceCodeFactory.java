package com.keimons.deepjson.compiler;

import com.keimons.deepjson.*;
import com.keimons.deepjson.annotation.CodecConfig;
import com.keimons.deepjson.annotation.Preset;
import com.keimons.deepjson.support.SyntaxToken;
import com.keimons.deepjson.util.ArrayUtil;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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

	@CodecConfig(format = @Preset(formatter = "ios"))
	private static final List<Class<?>> IMPORT = new ArrayList<Class<?>>();

	static {
		IMPORT.add(Config.class);
		IMPORT.add(CodecOptions.class);
		IMPORT.add(AbstractBuffer.class);
		IMPORT.add(AbstractContext.class);
		IMPORT.add(UnsafeUtil.class);
		IMPORT.add(Unsafe.class);
		IMPORT.add(ArrayList.class);
		IMPORT.add(TreeMap.class);
		IMPORT.add(List.class);
		IMPORT.add(ReaderBuffer.class);
		IMPORT.add(IDecodeContext.class);
		IMPORT.add(Field.class);
		IMPORT.add(Type.class);
		IMPORT.add(SyntaxToken.class);
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

		for (FieldInfo field : fields) {
			source.append("\tprivate Field $field$_")
					.append(field.getFieldName())
					.append(";\n")
			;
			source.append("\n");
		}

		source.append("\t@Override\n");
		source.append("\tpublic void init(Class<?> clazz) {\n");
		for (FieldInfo field : fields) {
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
		source.append("\tpublic void encode(AbstractContext context, AbstractBuffer buf, CodecModel model, Object object, int uniqueId, long options) {\n");
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

		TreeMap<Integer, ArrayList<FieldInfo>> map = makeCase(fields);

		source.append("\n");
		source.append("\t@Override\n");
		source.append("\tpublic Object decode(IDecodeContext context, ReaderBuffer buf, Type type, long options) {\n");
		source.append("\t\tClass<?> clazz = (Class<?>) type;\n");
		source.append("\t\tObject instance = newInstance(clazz);\n");
		source.append("\t\tfor (; ; ) {\n");
		source.append("\t\t\tSyntaxToken token = buf.nextToken();\n");
		source.append("\t\t\tif (token == SyntaxToken.RBRACE) {\n");
		source.append("\t\t\t\tbreak;\n");
		source.append("\t\t\t}\n");
		source.append("\t\t\tbuf.assertExpectedSyntax(SyntaxToken.STRING);\n");
		source.append("\t\t\tint index = switch0(buf);\n");
		source.append("\t\t\tbuf.nextToken();\n");
		source.append("\t\t\tbuf.assertExpectedSyntax(SyntaxToken.COLON);\n");
		source.append("\t\t\ttoken = buf.nextToken();\n");
		source.append("\t\t\tswitch (index) {\n");

		source.append("\t\t\t\tcase 0: {\n");
		source.append("\t\t\t\t\tcontext.put(buf.intValue(), instance);\n");
		source.append("\t\t\t\t}\n");
		source.append("\t\t\t\tbreak;\n");

		int count = 1;
		for (Map.Entry<Integer, ArrayList<FieldInfo>> entry : map.entrySet()) {
			for (FieldInfo info : entry.getValue()) {
				appendCase(source, count++, info);
			}
		}
		source.append("\t\t\t\tdefault:\n");
		source.append("\t\t\t}\n");
		source.append("\t\t\ttoken = buf.nextToken();\n");
		source.append("\t\t\tif (token == SyntaxToken.RBRACE) {\n");
		source.append("\t\t\t\tbreak;\n");
		source.append("\t\t\t}\n");
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
		source.append("import com.keimons.deepjson.support.SyntaxToken;\n");
		source.append("\n");
		source.append("import java.lang.reflect.Field;\n");
		source.append("import java.lang.reflect.Type;\n");

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
		source.append("\t\tif (context.isEmptyHead() && CodecOptions.IgnoreNonField.isOptions(options)) {\n");
		source.append("\t\t\tcontext.poll();\n");
		source.append("\t\t} else {\n");
		source.append("\t\t\tbuf.writeName(mark, $").append(ref).append(");\n");
		source.append("\t\t\tcontext.encode(buf, CodecModel.V, options);\n");
		source.append("\t\t\tmark = ',';\n");
		source.append("\t\t}\n");
	}

	public static TreeMap<Integer, ArrayList<FieldInfo>> makeCase(List<FieldInfo> fields) {
		TreeMap<Integer, ArrayList<FieldInfo>> map = new TreeMap<Integer, ArrayList<FieldInfo>>();
		for (FieldInfo field : fields) {
			int hashcode = ArrayUtil.hashcode(field.getWriteName().toCharArray());
			ArrayList<FieldInfo> list = map.get(hashcode);
			if (list == null) {
				list = new ArrayList<FieldInfo>();
				map.put(hashcode, list);
			}
			list.add(field);
		}
		return map;
	}

	public static void appendCase(StringBuilder source, int index, FieldInfo info) {
		source.append("\t\t\t\tcase ").append(index).append(": {\n");
		Class<?> type = info.getFieldType();
		String name = type.getName();
		if (type.isPrimitive()) {
			source.append("\t\t\t\t\t")
					.append(name)
					.append(" value = buf.")
					.append(name)
					.append("Value();\n");
			source.append("\t\t\t\t\tunsafe.put")
					.append(toUpperFirst(type.getName()))
					.append("(instance, ")
					.append(info.offset())
					.append("L, value);\n");
		} else {
			source.append("\t\t\t\t\tObject value;\n");
			source.append("\t\t\t\t\tif (token == SyntaxToken.NULL) {\n");
			source.append("\t\t\t\t\t\tvalue = null;\n");
			source.append("\t\t\t\t\t} else {\n");
			source.append("\t\t\t\t\t\tType ft = context.findType($field$_")
					.append(info.getFieldName()).append(");\n");
			source.append("\t\t\t\t\t\tvalue = context.decode(buf, ft, false, options);\n");
			source.append("\t\t\t\t\t}\n");
			source.append("\t\t\t\t\tunsafe.putObject(instance, ")
					.append(info.offset()).append("L, value);\n");
		}
		source.append("\t\t\t\t}\n");
		source.append("\t\t\t\tbreak;\n");
	}

	public static void appendSwitch(StringBuilder source, TreeMap<Integer, ArrayList<FieldInfo>> map) {
		source.append("\tprivate int switch0(ReaderBuffer buf) {\n");
		source.append("\t\tint hashcode = buf.valueHashcode();\n");
		source.append("\t\tswitch (hashcode) {\n");

		// 0 always @id

		int count = 1;
		for (Map.Entry<Integer, ArrayList<FieldInfo>> entry : map.entrySet()) {
			int hashcode = entry.getKey();
			source.append("\t\t\tcase ").append(hashcode).append(": {\n");
			for (FieldInfo info : entry.getValue()) {
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