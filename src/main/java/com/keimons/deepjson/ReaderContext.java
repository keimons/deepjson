package com.keimons.deepjson;

import com.keimons.deepjson.util.TypeNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.*;

/**
 * 上线文环境
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class ReaderContext {

	/**
	 * 增加一个对象
	 *
	 * @param uniqueId 唯一ID
	 * @param value    对象
	 */
	public abstract void put(int uniqueId, Object value);

	/**
	 * 获取一个对象
	 *
	 * @param uniqueId 对象唯一ID
	 * @return 对象
	 */
	public abstract @Nullable Object get(int uniqueId);

	/**
	 * 查找一个类型的实例类型
	 *
	 * @param type     类型 {@link Class}、{@link GenericArrayType}、{@link ParameterizedType}、
	 *                 {@link TypeVariable}、{@link WildcardType}中的一个。
	 * @param excepted 预期类型
	 * @return 实例类型
	 */
	public abstract Class<?> findInstanceType(Type type, Class<?> excepted);

	/**
	 * 查找一个类型的实例类型
	 *
	 * @param type 类型
	 * @return 有可能是任意类型或者为null，但是{@link Class}真实类型，{@link TypeVariable}泛型类型。
	 */
	public abstract @NotNull Type findInstanceType(TypeVariable<?> type);

	/**
	 * 查找{@link Class}中的泛型类型
	 * <p>
	 * 在上下文中查找泛型的实际类型，如果查找失败，抛出异常。
	 *
	 * @param target 泛型类型，例如{@link java.util.Map}
	 * @param name   泛型名，例如{@link java.util.Map}中的"K"
	 * @return 泛型的实际类型
	 * @throws TypeNotFoundException 类型查找失败
	 */
	public abstract Type findType(Class<?> target, String name);

	/**
	 * 对缓冲区中的内容进行解码
	 *
	 * @param reader  读取器
	 * @param type    解码目标类型
	 * @param options 解码选项
	 * @param <T>     解码目标类型
	 * @return 解码后对象
	 */
	public abstract <T> T decode(JsonReader reader, Type type, long options);

	/**
	 * 增加一个完成钩子
	 *
	 * @param hook 回调执行
	 */
	public abstract void addCompleteHook(Runnable hook);

	/**
	 * 增加一个钩子
	 *
	 * @param instance 实例
	 * @param offset   字段偏移
	 * @param uniqueId 引用
	 */
	public abstract void addCompleteHook(final Object instance, long offset, final int uniqueId);

	/**
	 * 增加一个钩子
	 *
	 * @param instance 实例
	 * @param setter   赋值
	 * @param uniqueId 引用
	 */
	public abstract void addCompleteHook(final Object instance, MethodHandle setter, final int uniqueId);

	/**
	 * 执行所有完成钩子
	 */
	public abstract void runCompleteHooks();

	/**
	 * 关闭缓冲区
	 *
	 * @param reader 读取器
	 */
	public abstract void close(JsonReader reader);
}