package com.keimons.deepjson.test.util;

import com.keimons.deepjson.util.ClassUtil;
import com.keimons.deepjson.util.UnsafeUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

/**
 * ClassUtil测试工具
 *
 * @author monkey1993
 * @version 1.0
 * @since 1.8
 **/
public class ClassUtilTest {

	@Test
	public void test() {
		List<Field> fields = ClassUtil.getFields(Node.class);
		for (Field field : fields) {
			System.out.println(field + " offset:" + UnsafeUtil.getUnsafe().objectFieldOffset(field));
		}
	}
}