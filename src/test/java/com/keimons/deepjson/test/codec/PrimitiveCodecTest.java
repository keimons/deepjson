package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

/**
 * 基础类型编解码测试
 * <p>
 * {@code boolean, byte, char, short, int, long, float, double}类型
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class PrimitiveCodecTest {

	@Test
	public void testEncode() {

	}

	@Test
	public void testDecode() {

		Boolean booleanValue1 = DeepJson.parseObject("/*true*/true", boolean.class);
		Boolean booleanValue2 = DeepJson.parseObject("false", Boolean.class);
		Boolean booleanValue3 = DeepJson.parseObject("\"false\"", boolean.class);
		System.out.println("boolean value: " + booleanValue1);
		System.out.println("boolean value: " + booleanValue2);
		System.out.println("boolean value: " + booleanValue3);

		String byteJson1 = "-128", byteJson2 = "\"127\"";
		Byte byteValue1 = DeepJson.parseObject(byteJson1, byte.class);
		Byte byteValue2 = DeepJson.parseObject(byteJson1, Byte.class);
		Byte byteValue3 = DeepJson.parseObject(byteJson2, byte.class);
		System.out.println("byte value: " + byteValue1);
		System.out.println("byte value: " + byteValue2);
		System.out.println("byte value: " + byteValue3);

		String charJson1 = "\"x\"", charJson2 = "\"a\"";
		Character charValue1 = DeepJson.parseObject(charJson1, char.class);
		Character charValue2 = DeepJson.parseObject(charJson1, Character.class);
		Character charValue3 = DeepJson.parseObject(charJson2, char.class);
		System.out.println("char value: " + charValue1);
		System.out.println("char value: " + charValue2);
		System.out.println("char value: " + charValue3);

		String shortJson1 = "1234", shortJson2 = "\"-1234\"";
		Short shortValue1 = DeepJson.parseObject(shortJson1, short.class);
		Short shortValue2 = DeepJson.parseObject(shortJson1, Short.class);
		Short shortValue3 = DeepJson.parseObject(shortJson2, short.class);
		System.out.println("short value: " + shortValue1);
		System.out.println("short value: " + shortValue2);
		System.out.println("short value: " + shortValue3);

		String intJson1 = "123456", intJson2 = "\"123456\"";
		Integer intValue1 = DeepJson.parseObject(intJson1, int.class);
		Integer intValue2 = DeepJson.parseObject(intJson1, Integer.class);
		Integer intValue3 = DeepJson.parseObject(intJson2, int.class);
		System.out.println("int value: " + intValue1);
		System.out.println("int value: " + intValue2);
		System.out.println("int value: " + intValue3);

		String longJson1 = "12345678901234l", longJson2 = "\"12345678901234L\"";
		Long longValue1 = DeepJson.parseObject(longJson1, long.class);
		Long longValue2 = DeepJson.parseObject(longJson1, Long.class);
		Long longValue3 = DeepJson.parseObject(longJson2, long.class);
		System.out.println("long value: " + longValue1);
		System.out.println("long value: " + longValue2);
		System.out.println("long value: " + longValue3);

		String floatJson1 = "1.28e-3f", floatJson2 = "\"-1.27E12f\"";
		Float floatValue1 = DeepJson.parseObject(floatJson1, float.class);
		Float floatValue2 = DeepJson.parseObject(floatJson1, Float.class);
		Float floatValue3 = DeepJson.parseObject(floatJson2, float.class);
		System.out.println("float value: " + floatValue1);
		System.out.println("float value: " + floatValue2);
		System.out.println("float value: " + floatValue3);

		String doubleJson1 = "1.28e-3d", doubleJson2 = "\"-1.27E12d\"";
		Double doubleValue1 = DeepJson.parseObject(doubleJson1, double.class);
		Double doubleValue2 = DeepJson.parseObject(doubleJson1, Double.class);
		Double doubleValue3 = DeepJson.parseObject(doubleJson2, double.class);
		System.out.println("double value: " + doubleValue1);
		System.out.println("double value: " + doubleValue2);
		System.out.println("double value: " + doubleValue3);
	}
}