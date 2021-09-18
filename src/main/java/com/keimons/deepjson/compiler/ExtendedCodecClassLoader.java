package com.keimons.deepjson.compiler;

import com.keimons.deepjson.support.codec.extended.ExtendedCodec;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.UnsafeUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 拓展编解码工具类类装载器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ExtendedCodecClassLoader extends SecureClassLoader {

	public static final String CLASS_EXTENSION = ".class";
	private static final ProtectionDomain DOMAIN;

	static {
		UnsafeUtil.getUnsafe().ensureClassInitialized(ExtendedCodec.class);
		DOMAIN = (ProtectionDomain) AccessController.doPrivileged(new PrivilegedAction<Object>() {
			@Override
			public Object run() {
				return ExtendedCodecClassLoader.class.getProtectionDomain();
			}
		});
	}

	private final Map<String, SourceCodeJavaFileObject> javaFileObjectMap = new ConcurrentHashMap<String, SourceCodeJavaFileObject>();

	public ExtendedCodecClassLoader(ClassLoader parentClassLoader) {
		super(parentClassLoader);
	}

	public ExtendedCodecClassLoader(String name, ClassLoader parentClassLoader) {
		super(name, parentClassLoader);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		SourceCodeJavaFileObject javaFileObject = javaFileObjectMap.get(name);
		if (null != javaFileObject) {
			byte[] byteCode = javaFileObject.getByteCode();
			try {
				if (PlatformUtil.javaVersion() >= 9) {
					return defineClass(byteCode);
				} else {
					return defineClass(name, byteCode, 0, byteCode.length, DOMAIN);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.findClass(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if (name.endsWith(CLASS_EXTENSION)) {
			String qualifiedClassName = name.substring(0, name.length() - CLASS_EXTENSION.length()).replace('/', '.');
			SourceCodeJavaFileObject javaFileObject = javaFileObjectMap.get(qualifiedClassName);
			if (null != javaFileObject && null != javaFileObject.getByteCode()) {
				return new ByteArrayInputStream(javaFileObject.getByteCode());
			}
		}
		return super.getResourceAsStream(name);
	}

	/**
	 * 暂时存放编译的源文件对象,key为全类名的别名（非URI模式）,如club.throwable.compile.HelloService
	 */
	void addJavaFileObject(String qualifiedClassName, SourceCodeJavaFileObject javaFileObject) {
		javaFileObjectMap.put(qualifiedClassName, javaFileObject);
	}

	Collection<SourceCodeJavaFileObject> listJavaFileObject() {
		return Collections.unmodifiableCollection(javaFileObjectMap.values());
	}

	/**
	 * 类装载（使用Java 9中暴露出来新的类装载方法）
	 * <p>
	 * 使用指定类的装载器和保护域装载一个新类。
	 * <p>
	 * 内部调用 SharedSecrets.getJavaLangAccess().defineClass()，我们发现，
	 * 在不同的jdk中，JavaLangAccess和SharedSecrets使用不同的实现方式的，例如：
	 * 虽然同样是Oracle Corporation规范，但是，是不同的jdk依然可以有自己的实现，
	 * 在Amazon Corretto和在Oracle Corporation中，他们的位置并不相同。应该尽量
	 * 避免直接使用过于底层的方法。
	 *
	 * @param classCode 字节码
	 * @return Class
	 * @throws Exception 权限不足
	 * @see MethodHandles.Lookup#defineClass(byte[]) 指定的类环境进行装载
	 * @since 9
	 */
	private Class<?> defineClass(byte[] classCode) throws Exception {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		lookup = MethodHandles.privateLookupIn(ExtendedCodec.class, lookup);
		return lookup.defineClass(classCode);
	}
}