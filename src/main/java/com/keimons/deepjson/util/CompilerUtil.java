package com.keimons.deepjson.util;

import com.keimons.deepjson.ICodec;
import com.keimons.deepjson.compiler.SourceCodeCompiler;

/**
 * 编译工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CompilerUtil {

	public static final String util = "package java.lang;\n" +
			"\n" +
			"public class StringBundle extends java.lang.AbstractStringBuilder {\n" +
			"\n" +
			"\t@Override\n" +
			"\tpublic String toString() {\n" +
			"\t\treturn \"\";\n" +
			"\t}\n" +
			"}";

	private static final SourceCodeCompiler COMPILER = new SourceCodeCompiler();

	public static Class<? extends ICodec<?>> compiler(
			String packageName, String className, String sourceCode) {
		return COMPILER.compiler(packageName, className, sourceCode);
	}

	public static Class<?> compiler() {
		return COMPILER.compiler("java.lang", "StringBundle", util);
	}
}