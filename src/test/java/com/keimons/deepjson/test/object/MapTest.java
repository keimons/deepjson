package com.keimons.deepjson.test.object;

import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class MapTest {

	@Test
	public void testPrimitive() {
		Map<Object, Object> map = new HashMap<>();
		map.put(false, true);
		map.put('a', 'b');
		map.put((byte) 1, (byte) 1);
		map.put((short) 2, (short) 2);
		map.put(3, 3);
		map.put(4L, 4L);
		map.put(5f, 5f);
		map.put(6d, 6d);
		System.out.println(DeepJson.toJsonString(map));
		System.out.println(DeepJson.toJsonString(map, CodecOptions.PrimitiveKey));
	}
}