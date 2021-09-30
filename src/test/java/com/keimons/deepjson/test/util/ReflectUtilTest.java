package com.keimons.deepjson.test.util;

import com.keimons.deepjson.Config;
import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.ReflectUtil;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * {@link ReflectUtil}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReflectUtilTest<K, V, T1 extends Map<?, ?> & Serializable, T2 extends Map<K, V> & Serializable> {

	@Test
	public void test() throws Exception {
		Type type1 = ClassUtil.findGenericType(ReflectUtilTest.class, ReflectUtilTest.class, "T1");
		Type type2 = ClassUtil.findGenericType(ReflectUtilTest.class, ReflectUtilTest.class, "T2");
		TypeVariable<?> variable1 = type1 instanceof TypeVariable<?> ? ((TypeVariable<?>) type1) : null;
		TypeVariable<?> variable2 = type2 instanceof TypeVariable<?> ? ((TypeVariable<?>) type1) : null;
		System.out.println(Config.getType(variable1.getBounds()));
		System.out.println(Config.getType(variable2.getBounds()));
	}
}