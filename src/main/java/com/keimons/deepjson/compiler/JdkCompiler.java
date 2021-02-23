package com.keimons.deepjson.compiler;

import com.keimons.deepjson.serializer.ISerializerWriter;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JdkCompiler. (SPI, Singleton, ThreadSafe)
 *
 * @author xiemalin
 * @since 1.0.0
 */
public class JdkCompiler {

	private static final DeepClassLoader classLoader;

	public static final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

	static {
		classLoader = AccessController.doPrivileged((PrivilegedAction<DeepClassLoader>) DeepClassLoader::new);
	}

	private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap<>();

	public static Class<? extends ISerializerWriter> compiler(String code) {
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		JavaFileManager javaFileManager = new DeepJavaFileManager(
				compiler.getStandardFileManager(collector, null, StandardCharsets.UTF_8)
		);

		List<String> options = new ArrayList<>();
		options.add("-source");
		options.add("1.6");
		options.add("-target");
		options.add("1.6");

		Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
		Matcher matcher = CLASS_PATTERN.matcher(code);
		String simpleName;
		if (matcher.find()) {
			simpleName = matcher.group(1);
		} else {
			throw new IllegalArgumentException("No such class name in " + code);
		}
		String className = "com.keimons.deepjson.serializer." + simpleName;

		JavaFileObject javaFileObject = new DeepJavaFileObject(simpleName, code);
		Boolean result = compiler.getTask(
				null,
				javaFileManager,
				collector,
				options,
				null,
				Collections.singletonList(javaFileObject)
		).call();

		System.out.println(result);

		ClassLoader classloader = new DeepClassLoader();

		Class<?> clazz = null;
		try {
			clazz = classloader.loadClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return (Class<? extends ISerializerWriter>) clazz;
	}

	public static class DeepClassLoader extends ClassLoader {

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			JavaFileObject fileObject = fileObjects.get(name);
			if (fileObject != null) {
				byte[] bytes = ((DeepJavaFileObject) fileObject).getCompiledBytes();
				return defineClass(name, bytes, 0, bytes.length);
			}
			try {
				return ClassLoader.getSystemClassLoader().loadClass(name);
			} catch (Exception e) {
				return super.findClass(name);
			}
		}
	}

	public static class DeepJavaFileObject extends SimpleJavaFileObject {

		private CharSequence source;
		private ByteArrayOutputStream outPutStream;


		public DeepJavaFileObject(String name, String source) {
			super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source = source;
		}

		public DeepJavaFileObject(String name, Kind kind) {
			super(URI.create("String:///" + name + kind.extension), kind);
			source = null;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return source;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			if (outPutStream == null) {
				outPutStream = new ByteArrayOutputStream();
			}
			return outPutStream;
		}

		public byte[] getCompiledBytes() {
			return outPutStream.toByteArray();
		}
	}

	public static class DeepJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

		protected DeepJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
		}

		@Override
		public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
			JavaFileObject javaFileObject = fileObjects.get(className);
			if (javaFileObject == null) {
				super.getJavaFileForInput(location, className, kind);
			}
			return javaFileObject;
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			JavaFileObject javaFileObject = new DeepJavaFileObject(qualifiedClassName, kind);
			fileObjects.put(qualifiedClassName, javaFileObject);
			return javaFileObject;
		}
	}
}