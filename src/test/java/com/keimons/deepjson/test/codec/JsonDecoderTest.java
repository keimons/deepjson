package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.JsonObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonDecoderTest {

	@Test
	public void test() {
		Map obj = new JsonObject();
		Type type = obj.getClass().getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			System.out.println(111);
		} else {
			System.out.println(222);
		}

//		String json = "{\"@id\":1,\"key\":\"value\",\"loop\":{\"$id\":1}}";
//		JsonObject map = DeepJson.parseObject(json, JsonObject.class);
//		System.out.println(map == map.get("loop"));
	}
}