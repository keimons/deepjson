package com.keimons.deepjson.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 源代码的Java类
 *
 * @author monkey
 * @version 1.0
 * @since 1.8
 **/
public class SourceCodeJavaFileObject extends SimpleJavaFileObject {

	public static final String JAVA_EXTENSION = ".java";

	private static URI fromClassName(String className) {
		try {
			return new URI(className);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(className, e);
		}
	}

	/**
	 * 字节流输出
	 */
	private ByteArrayOutputStream byteCode;

	/**
	 * 源代码（标准Java文件）
	 */
	private final CharSequence sourceCode;

	public SourceCodeJavaFileObject(String className, CharSequence sourceCode) {
		super(fromClassName(className + JAVA_EXTENSION), Kind.SOURCE);
		this.sourceCode = sourceCode;
	}

	public SourceCodeJavaFileObject(String fullClassName, Kind kind) {
		super(fromClassName(fullClassName), kind);
		this.sourceCode = null;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		return sourceCode;
	}

	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(getByteCode());
	}

	// 注意这个方法是编译结果回调的OutputStream，回调成功后就能通过下面的getByteCode()方法获取目标类编译后的字节码字节数组
	@Override
	public OutputStream openOutputStream() {
		return byteCode = new ByteArrayOutputStream();
	}

	public byte[] getByteCode() {
		return byteCode.toByteArray();
	}
}