package com.keimons.deepjson.test.verification;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.util.CodecUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 验证
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class VerificationTest {

	@Test
	public void char2Test() {
		String value = "𥔲";
		System.out.println(CodecUtil.length(value));
		String result = DeepJson.toJsonString(value);
		System.out.println(result);
		Map<String, String> map = new HashMap<>();
		map.put(value, value);
		result = DeepJson.toJsonString(map);
		System.out.println(result);
		List<String> list = new ArrayList<>();
		list.add("t");
		list.add("e");
		list.add("s");
		list.add("t");
		list.add(value);
		result = DeepJson.toJsonString(list);
		System.out.println(result);
		System.out.println(Arrays.toString(value.getBytes(StandardCharsets.UTF_8)));
	}
}