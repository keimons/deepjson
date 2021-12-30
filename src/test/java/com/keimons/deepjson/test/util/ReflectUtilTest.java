package com.keimons.deepjson.test.util;

import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.internal.util.GenericUtil;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * {@link GenericUtil}测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReflectUtilTest<K, V, T1 extends Map<?, ?> & Serializable, T2 extends Map<K, V> & Serializable> {

	@Test
	public void test() throws Exception {
		Type type1 = GenericUtil.findGenericType(ReflectUtilTest.class, ReflectUtilTest.class, "T1");
		Type type2 = GenericUtil.findGenericType(ReflectUtilTest.class, ReflectUtilTest.class, "T2");
		TypeVariable<?> variable1 = type1 instanceof TypeVariable<?> ? ((TypeVariable<?>) type1) : null;
		TypeVariable<?> variable2 = type2 instanceof TypeVariable<?> ? ((TypeVariable<?>) type1) : null;
		System.out.println(CodecConfig.getType(variable1.getBounds()));
		System.out.println(CodecConfig.getType(variable2.getBounds()));
	}
}