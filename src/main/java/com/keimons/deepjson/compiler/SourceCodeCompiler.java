package com.keimons.deepjson.compiler;

import com.keimons.deepjson.serializer.ISerializerWriter;

import javax.tools.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 源代码编译工具
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class SourceCodeCompiler {

	private static final String PACKAGE = "com/keimons/deepjson/compiler/";

	/**
	 * 编译诊断收集器
	 */
	private static final DiagnosticCollector<JavaFileObject> DIAGNOSTIC_COLLECTOR = new DiagnosticCollector<>();

	@SuppressWarnings("unchecked")
	public static Class<? extends ISerializerWriter> compiler(String source, String className) {
		// 获取系统编译器实例
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// 设置编译参数 - 指定编译版本为 JDK 11
		List<String> options = new ArrayList<>();
		options.add("-source");
		options.add("11");
		options.add("-target");
		options.add("11");
		options.add("--class-path");
		String path = System.getProperty("jdk.module.path") +
				System.getProperty("path.separator") +
				System.getProperty("java.class.path");
		URL classes = SourceCodeCompiler.class.getResource("");
		if (classes != null) {
			path += System.getProperty("path.separator");
			path += classes.getPath().replace(PACKAGE, "");
		}
		options.add(path);
		// 获取标准的Java文件管理器实例
		StandardJavaFileManager manager = compiler.getStandardFileManager(DIAGNOSTIC_COLLECTOR, null, null);
		// 初始化自定义类加载器
		SourceCodeClassLoader classLoader = new SourceCodeClassLoader(Thread.currentThread().getContextClassLoader());
		// 初始化自定义Java文件管理器实例
		SourceCodeJavaFileManager fileManager = new SourceCodeJavaFileManager(manager, classLoader);
		String packageName = "com.keimons.deepjson.serializer";
		String qualifiedName = packageName + "." + className;
		SourceCodeJavaFileObject javaFileObject = new SourceCodeJavaFileObject(className, source);
		// 添加Java源文件实例到自定义Java文件管理器实例中
		fileManager.addJavaFileObject(
				StandardLocation.SOURCE_PATH,
				packageName,
				className + SourceCodeJavaFileObject.JAVA_EXTENSION,
				javaFileObject
		);
		// 初始化一个编译任务实例
		JavaCompiler.CompilationTask compilationTask = compiler.getTask(
				null,
				fileManager,
				DIAGNOSTIC_COLLECTOR,
				options,
				null,
				Collections.singletonList(javaFileObject)
		);
		// 执行编译任务
		Boolean result = compilationTask.call();
		if (result == null || !result) {
			System.out.println("Compiler Fail.");
			DIAGNOSTIC_COLLECTOR.getDiagnostics();
		}
		Class<?> clazz = null;
		try {
			clazz = classLoader.loadClass(qualifiedName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (Class<? extends ISerializerWriter>) clazz;
	}
}