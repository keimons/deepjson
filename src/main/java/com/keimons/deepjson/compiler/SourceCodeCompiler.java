package com.keimons.deepjson.compiler;

import com.keimons.deepjson.serializer.ISerializer;

import javax.tools.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 源代码编译工具
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class SourceCodeCompiler {

	private static final String PACKAGE = "com/keimons/deepjson/compiler/";

	@SuppressWarnings("unchecked")
	public Class<? extends ISerializer> compiler(String packageName, String className, String source) {
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

		// 编译诊断收集器
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

		// 获取标准的Java文件管理器实例
		StandardJavaFileManager manager = compiler.getStandardFileManager(collector, null, null);
		// 初始化自定义类加载器
		SourceCodeClassLoader classLoader = new SourceCodeClassLoader(Thread.currentThread().getContextClassLoader());
		// 初始化自定义Java文件管理器实例
		SourceCodeJavaFileManager fileManager = new SourceCodeJavaFileManager(manager, classLoader);
		String fullName = packageName + "." + className;
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
				collector,
				options,
				null,
				Collections.singletonList(javaFileObject)
		);
		// 执行编译任务
		Boolean result = compilationTask.call();
		if (result == null || !result) {
			throw new IllegalStateException(
					"Compilation failed. class: " + className + ", diagnostics: " + collector.getDiagnostics());
		}
		Class<?> clazz = null;
		try {
			clazz = classLoader.loadClass(fullName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (Class<? extends ISerializer>) clazz;
	}
}