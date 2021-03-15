package com.keimons.deepjson.compiler;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 源代码类加载器
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class SourceCodeClassLoader extends ClassLoader {

	public static final String CLASS_EXTENSION = ".class";

	private final Map<String, JavaFileObject> javaFileObjectMap = new ConcurrentHashMap<>();

	public SourceCodeClassLoader(ClassLoader parentClassLoader) {
		super(parentClassLoader);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		JavaFileObject javaFileObject = javaFileObjectMap.get(name);
		if (null != javaFileObject) {
			SourceCodeJavaFileObject sourceCodeJavaFileObject = (SourceCodeJavaFileObject) javaFileObject;
			byte[] byteCode = sourceCodeJavaFileObject.getByteCode();
			return defineClass(name, byteCode, 0, byteCode.length);
		}
		return super.findClass(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if (name.endsWith(CLASS_EXTENSION)) {
			String qualifiedClassName = name.substring(0, name.length() - CLASS_EXTENSION.length()).replace('/', '.');
			SourceCodeJavaFileObject javaFileObject = (SourceCodeJavaFileObject) javaFileObjectMap.get(qualifiedClassName);
			if (null != javaFileObject && null != javaFileObject.getByteCode()) {
				return new ByteArrayInputStream(javaFileObject.getByteCode());
			}
		}
		return super.getResourceAsStream(name);
	}

	/**
	 * 暂时存放编译的源文件对象,key为全类名的别名（非URI模式）,如club.throwable.compile.HelloService
	 */
	void addJavaFileObject(String qualifiedClassName, JavaFileObject javaFileObject) {
		javaFileObjectMap.put(qualifiedClassName, javaFileObject);
	}

	Collection<JavaFileObject> listJavaFileObject() {
		return Collections.unmodifiableCollection(javaFileObjectMap.values());
	}
}