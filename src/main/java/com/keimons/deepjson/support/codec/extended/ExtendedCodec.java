package com.keimons.deepjson.support.codec.extended;

import com.keimons.deepjson.JsonReader;
import com.keimons.deepjson.ReaderContext;
import com.keimons.deepjson.SyntaxToken;
import com.keimons.deepjson.compiler.ExtendedCodecClassLoader;
import com.keimons.deepjson.internal.util.ConstructorUtils;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.codec.AbstractOnlineCodec;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

/**
 * 拓展编解码器
 * <p>
 * 将所有的非系统提供的编解码方案识别为第三方拓展，所有的拓展都应该继承自{@link ExtendedCodec}。
 * 所有拓展使用{@code com.keimons.deepjson.support.codec.extended}路径。
 * <p>
 * 这个类存在的意义是确保这个包能被初始化，保证第三方拓展编解码方案能被识别为DeepJson的模块。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see ExtendedCodecClassLoader 拓展编解码方案装载器
 * @since 1.6
 **/
public abstract class ExtendedCodec extends AbstractOnlineCodec<Object> {

	/**
	 * 编解码对象类型
	 */
	protected Class<?> clazz;

	/**
	 * 是否{@code lambda}表达式
	 */
	private boolean isLambda;

	/**
	 * 确定class可以被实例化
	 *
	 * @param clazz 要判断的class
	 * @throws IncompatibleTypeException 不兼容接口或抽象类
	 */
	protected void acceptInstantiation(Class<?> clazz) {
		if (clazz.isArray() || clazz.isEnum() || clazz.isPrimitive() ||
				clazz.isInterface() || clazz.isAnnotation() ||
				Modifier.isAbstract(clazz.getModifiers())) {
			throw new IncompatibleTypeException("cannot instantiation of class " + clazz.getName());
		}
		this.clazz = clazz;
		this.isLambda = ClassUtil.isLambda(clazz);
	}

	/**
	 * 创建一个对象实例
	 *
	 * @param clazz 对象
	 * @param <T>   对象类型
	 * @return 新实例
	 */
	protected <T> T newInstance(Class<T> clazz) {
		try {
			if (isLambda) { // 针对于lambda直接分配内存
				@SuppressWarnings("unchecked")
				T instance = (T) unsafe.allocateInstance(clazz);
				return instance;
			} else {
				return ReflectionUtil.newInstance(clazz);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取类中的字段
	 *
	 * @param clazz     获取字段的类
	 * @param fieldName 字段名
	 * @param isPublic  是否公共的
	 * @return 类中的字段
	 */
	public static Field findField(Class<?> clazz, String fieldName, boolean isPublic) {
		try {
			if (isPublic) {
				return clazz.getField(fieldName);
			} else {
				return clazz.getDeclaredField(fieldName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 第三方拓展序列化
	 * <p>
	 * 严格限制@code type}是{@link Class}或是使用边界限定的{@link TypeVariable}，其他情况抛出异常。
	 * <p>
	 * 当使用边界时，需要判断所有边界是否合法，如果
	 *
	 * @param context 上线文环境
	 * @param reader  读取器
	 * @param clazz   编解码器类型，确保type是{@link Class}或{@link TypeVariable}。
	 * @param options 解码选项
	 * @return 对象实例
	 */
	@Override
	public Object decode(ReaderContext context, JsonReader reader, Class<?> clazz, long options) {
		try {
			SyntaxToken token = reader.token();
			if (token == SyntaxToken.NULL) {
				return null;
			}
			token = reader.nextToken();
			if (token == SyntaxToken.STRING && reader.checkGetType()) {
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.COLON);
				reader.nextToken();
				reader.assertExpectedSyntax(SyntaxToken.STRING);
				String className = reader.stringValue();
				token = reader.nextToken();
				if (token == SyntaxToken.RBRACE) {
					return null;
				}
				reader.nextToken();
				Class<?> excepted = ClassUtil.findClass(className); // 解析类中的名字
				if (excepted == this.clazz) {
					return decode0(context, reader, (Class<?>) clazz, options);
				}
				if (this.clazz.isAssignableFrom(excepted) || excepted.isAssignableFrom(this.clazz)) {
					return decode0(context, reader, excepted, options);
				} else {
					throw new IncompatibleTypeException(this.clazz, excepted);
				}
			}
			// 确定这是一个class
			if (this.clazz == clazz) {
				return decode0(context, reader, clazz, options);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		throw new IncompatibleTypeException(this.clazz, clazz);
	}

	/**
	 * 初始化拓展编解码器
	 *
	 * @param clazz 编解码对象类型
	 */
	public abstract void init(Class<?> clazz);

	protected abstract Object decode0(ReaderContext context, JsonReader reader, Class<?> clazz, long options) throws Throwable;

	/**
	 * 根据类的构造方法，查找可用的编解码器
	 *
	 * @param clazz 即将生成编解码器的类
	 * @return 编解码器
	 */
	public static ExtendedCodec create(Class<?> clazz) {
		if (clazz.isSynthetic()) {
			if (ClassUtil.isLambda(clazz)) {
				return new ParameterlessEntityCodec();
			}
		}
		Constructor<?> constructor = ConstructorUtils.findConstructor(clazz);
		if (constructor.getParameterCount() == 0) {
			return new ParameterlessEntityCodec();
		} else {
			return new ParameterizedEntityCodec();
		}
	}
}