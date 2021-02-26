package com.keimons.deepjson.serializer;

import com.keimons.deepjson.compiler.SourceCodeCompiler;

/**
 * 编译工具
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class CompilerUtil {

	private static final SourceCodeCompiler COMPILER = new SourceCodeCompiler();

	public static Class<? extends ISerializer> compiler(
			String packageName, String className, String sourceCode) {
		return COMPILER.compiler(packageName, className, sourceCode);
	}
}