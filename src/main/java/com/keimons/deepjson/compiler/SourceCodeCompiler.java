package com.keimons.deepjson.compiler;

import com.keimons.deepjson.support.codec.extended.ExtendedCodec;
import com.keimons.deepjson.util.PlatformUtil;

import javax.tools.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 源代码编译工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class SourceCodeCompiler {

	private static final String PACKAGE = "com/keimons/deepjson/compiler/";

	// 初始化自定义类加载器
	ExtendedCodecClassLoader classLoader;

	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (PlatformUtil.javaVersion() <= 8) {
			classLoader = new ExtendedCodecClassLoader(loader);
		} else {
			classLoader = new ExtendedCodecClassLoader("DeepJson", loader);
		}
	}

	@SuppressWarnings("unchecked")
	public Class<? extends ExtendedCodec> compiler(String packageName, String className, String source) {
		// 获取系统编译器实例
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// 设置编译参数 - 指定编译版本为 JDK 7和JDK 9
		List<String> options = new ArrayList<String>();
		if (PlatformUtil.javaVersion() <= 8) {
			options.add("-source");
			options.add("6");
			options.add("-target");
			options.add("6");
		} else {
			options.add("-source");
			options.add("9");
			options.add("-target");
			options.add("9");
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
		}

		// 编译诊断收集器
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<JavaFileObject>();

		// 获取标准的Java文件管理器实例
		StandardJavaFileManager manager = compiler.getStandardFileManager(collector, null, null);
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
		return (Class<? extends ExtendedCodec>) clazz;
	}
}