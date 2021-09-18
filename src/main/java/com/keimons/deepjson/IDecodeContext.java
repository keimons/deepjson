package com.keimons.deepjson;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 上线文环境
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface IDecodeContext {

	/**
	 * 增加一个对象
	 *
	 * @param uniqueId 唯一ID
	 * @param value    对象
	 */
	void put(int uniqueId, Object value);

	/**
	 * 获取一个对象
	 *
	 * @param uniqueId 对象唯一ID
	 * @return 对象
	 */
	@Nullable Object get(int uniqueId);

	Type findType(Class<?> target, String name);

	Type findType(Field field);

	/**
	 * 对于缓冲区中的内容进行解码
	 *
	 * @param buf     缓冲区
	 * @param type    解码目标类型
	 * @param options 解码选项
	 */
	<T> T decode(ReaderBuffer buf, Type type, long options);

	/**
	 * 对于缓冲区中的内容进行解码
	 *
	 * @param buf     缓冲区
	 * @param type    解码目标类型
	 * @param options 解码选项
	 * @param next    是否读取下一个
	 */
	<T> T decode(ReaderBuffer buf, Type type, long options, boolean next);

	/**
	 * 增加一个完成钩子
	 *
	 * @param hook 回调执行
	 */
	void addCompleteHook(Runnable hook);

	/**
	 * 执行所有完成钩子
	 */
	void runCompleteHooks();

	/**
	 * 关闭缓冲区
	 *
	 * @param buf 缓冲区
	 */
	void close(ReaderBuffer buf);
}