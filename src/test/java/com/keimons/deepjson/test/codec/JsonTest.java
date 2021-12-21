package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.Json;
import com.keimons.deepjson.test.AssertUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Json}编解码测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class JsonTest {

	@Test
	public void testJson() {
		Json json = new Json();

		json.put("test1", 1024);
		json.put("test2", 1024L);
		json.put("test3", "test1");

		String str1 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("Json编码测试", str1, "{\"test2\":1024,\"test3\":\"test1\",\"test1\":1024}");

		Json r1 = DeepJson.parseObject(str1);
		AssertUtil.assertEquals("Json解码测试", json, r1);

		json.clear();
		String str2 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("Json清空测试", str2, "{}");

		json.add("test1");
		json.add("test2");
		json.add("test3");
		json.add(1024);
		json.add(1024L);

		String str3 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("Json编码测试", str3, "[\"test1\",\"test2\",\"test3\",1024,1024]");

		Json r2 = DeepJson.parseObject(str3);
		AssertUtil.assertEquals("Json解码测试", json, r2);

		json.clear();
		String str4 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("Json清空测试", str4, "{}");
	}

	@Test
	public void testJsonObject() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("test0", 0);
		map.put("test1", 1);
		map.put("test2", 2);
		Json json = new Json(map);

		String str1 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("JsonObject编码测试", str1, "{\"test0\":0,\"test1\":1,\"test2\":2}");

		String str2 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("JsonObject编码测试", str2, "{\"test0\":0,\"test1\":1,\"test2\":2}");

		Object none = json.removeKey("test3");
		AssertUtil.assertNull("JsonObject移除测试", none);

		Object t2 = json.removeKey("test2");
		AssertUtil.assertEquals("JsonObject移除测试", 2, t2);

		String str3 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("JsonObject编码测试", str3, "{\"test0\":0,\"test1\":1}");

		json.clear();
		String str4 = DeepJson.toJsonString(json);
		AssertUtil.assertEquals("JsonObject清空测试", str4, "{}");
	}
}