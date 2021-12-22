package com.keimons.deepjson.test.codec.array;

import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 对象数组类型编解码测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ArrayTest {

	@Test
	public void testEmptyBooleanArray() {
		String json = DeepJson.toJsonString(new boolean[0]);
		AssertUtil.assertEquals("空Boolean数组编码测试", json, "[]");

		boolean[] newArray = DeepJson.parseObject(json, boolean[].class);
		AssertUtil.assertNotNull("空Boolean数组解码测试", newArray);
		AssertUtil.assertTrue("空Boolean数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyCharArray() {
		String json = DeepJson.toJsonString(new char[0]);
		AssertUtil.assertEquals("空Char数组编码测试", json, "[]");

		char[] newArray = DeepJson.parseObject(json, char[].class);
		AssertUtil.assertNotNull("空Char数组解码测试", newArray);
		AssertUtil.assertTrue("空Char数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyByteArray() {
		String json = DeepJson.toJsonString(new byte[0]);
		AssertUtil.assertEquals("空Byte数组编码测试", json, "[]");

		byte[] newArray = DeepJson.parseObject(json, byte[].class);
		AssertUtil.assertNotNull("空Byte数组解码测试", newArray);
		AssertUtil.assertTrue("空Byte数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyShortArray() {
		String json = DeepJson.toJsonString(new short[0]);
		AssertUtil.assertEquals("空Short数组编码测试", json, "[]");

		short[] newArray = DeepJson.parseObject(json, short[].class);
		AssertUtil.assertNotNull("空Short数组解码测试", newArray);
		AssertUtil.assertTrue("空Short数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyIntArray() {
		String json = DeepJson.toJsonString(new int[0]);
		AssertUtil.assertEquals("空Int数组编码测试", json, "[]");

		int[] newArray = DeepJson.parseObject(json, int[].class);
		AssertUtil.assertNotNull("空Int数组解码测试", newArray);
		AssertUtil.assertTrue("空Int数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyLongArray() {
		String json = DeepJson.toJsonString(new long[0]);
		AssertUtil.assertEquals("空Long数组编码测试", json, "[]");

		long[] newArray = DeepJson.parseObject(json, long[].class);
		AssertUtil.assertNotNull("空Long数组解码测试", newArray);
		AssertUtil.assertTrue("空Long数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyFloatArray() {
		String json = DeepJson.toJsonString(new float[0]);
		AssertUtil.assertEquals("空Float数组编码测试", json, "[]");

		float[] newArray = DeepJson.parseObject(json, float[].class);
		AssertUtil.assertNotNull("空Float数组解码测试", newArray);
		AssertUtil.assertTrue("空Float数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyDoubleArray() {
		String json = DeepJson.toJsonString(new double[0]);
		AssertUtil.assertEquals("空Double数组编码测试", json, "[]");

		double[] newArray = DeepJson.parseObject(json, double[].class);
		AssertUtil.assertNotNull("空Double数组解码测试", newArray);
		AssertUtil.assertTrue("空Double数组解码测试", newArray.length == 0);
	}

	@Test
	public void testEmptyObjectArray() {
		String json = DeepJson.toJsonString(new Object[0]);
		AssertUtil.assertEquals("空Object数组编码测试", json, "[]");

		Object[] newArray = DeepJson.parseObject(json, Object[].class);
		AssertUtil.assertNotNull("空Object数组解码测试", newArray);
		AssertUtil.assertTrue("空Object数组解码测试", newArray.length == 0);
	}

	@Test
	public void testFullBooleanArray() {
		boolean[] array = {true, false, true, false};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Boolean数组编码测试", json, "[true,false,true,false]");

		boolean[] newArray = DeepJson.parseObject(json, boolean[].class);
		AssertUtil.assertNotNull("Boolean数组解码测试", newArray);
		AssertUtil.assertTrue("Boolean数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullCharArray() {
		char[] array = {'k', 'e', 'i', 'm', 'o', 'n', 's'};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Char数组编码测试", json, "[\"k\",\"e\",\"i\",\"m\",\"o\",\"n\",\"s\"]");

		char[] newArray = DeepJson.parseObject(json, char[].class);
		AssertUtil.assertNotNull("Char数组解码测试", newArray);
		AssertUtil.assertTrue("Char数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullByteArray() {
		byte[] array = {0, 1, -1, 127, -128};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Byte数组编码测试", json, "[0,1,-1,127,-128]");

		byte[] newArray = DeepJson.parseObject(json, byte[].class);
		AssertUtil.assertNotNull("Byte数组解码测试", newArray);
		AssertUtil.assertTrue("Byte数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullShortArray() {
		short[] array = {0, 1, -1, 32767, -32768};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Short数组编码测试", json, "[0,1,-1,32767,-32768]");

		short[] newArray = DeepJson.parseObject(json, short[].class);
		AssertUtil.assertNotNull("Short数组解码测试", newArray);
		AssertUtil.assertTrue("Short数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullIntArray() {
		int[] array = {0, 1, -1, 2147483647, -2147483648};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Int数组编码测试", json, "[0,1,-1,2147483647,-2147483648]");

		int[] newArray = DeepJson.parseObject(json, int[].class);
		AssertUtil.assertNotNull("Int数组解码测试", newArray);
		AssertUtil.assertTrue("Int数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullLongArray() {
		long[] array = {0, 1, -1, 9223372036854775807L, -9223372036854775808L};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Long数组编码测试", json, "[0,1,-1,9223372036854775807,-9223372036854775808]");

		long[] newArray = DeepJson.parseObject(json, long[].class);
		AssertUtil.assertNotNull("Long数组解码测试", newArray);
		AssertUtil.assertTrue("Long数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullFloatArray() {
		float[] array = {0, 1, -1, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.MIN_VALUE, Float.MAX_VALUE};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Float数组编码测试", json, "[0.0,1.0,-1.0,NaN,Infinity,-Infinity,1.4E-45,3.4028235E38]");

		float[] newArray = DeepJson.parseObject(json, float[].class);
		AssertUtil.assertNotNull("Float数组解码测试", newArray);
		AssertUtil.assertTrue("Float数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullDoubleArray() {
		double[] array = {0, 1, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MIN_VALUE, Double.MAX_VALUE};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Double数组编码测试", json, "[0.0,1.0,-1.0,NaN,Infinity,-Infinity,4.9E-324,1.7976931348623157E308]");

		double[] newArray = DeepJson.parseObject(json, double[].class);
		AssertUtil.assertNotNull("Double数组解码测试", newArray);
		AssertUtil.assertTrue("Double数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFullObjectArray() {
		Node[] array = {new Node(1), new Node(2), new Node(3)};
		String json = DeepJson.toJsonString(array);
		AssertUtil.assertEquals("Object数组编码测试", json, "[{\"value\":1},{\"value\":2},{\"value\":3}]");

		Node[] newArray = DeepJson.parseObject(json, Node[].class);
		AssertUtil.assertNotNull("Object数组解码测试", newArray);
		AssertUtil.assertTrue("Object数组解码测试", Arrays.equals(array, newArray));
	}

	@Test
	public void testFieldBooleanArray() {
		BooleanArrayNode node = new BooleanArrayNode();
		node.value = new boolean[]{true, false, true, false};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Boolean数组字段编码测试", json, "{\"value\":[true,false,true,false]}");

		BooleanArrayNode newNode = DeepJson.parseObject(json, BooleanArrayNode.class);
		AssertUtil.assertNotNull("Boolean数组字段解码测试", newNode);
		AssertUtil.assertTrue("Boolean数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldCharArray() {
		CharArrayNode node = new CharArrayNode();
		node.value = new char[]{'k', 'e', 'i', 'm', 'o', 'n', 's'};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Char数组字段编码测试", json, "{\"value\":[\"k\",\"e\",\"i\",\"m\",\"o\",\"n\",\"s\"]}");

		CharArrayNode newNode = DeepJson.parseObject(json, CharArrayNode.class);
		AssertUtil.assertNotNull("Char数组字段解码测试", newNode);
		AssertUtil.assertTrue("Char数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldByteArray() {
		ByteArrayNode node = new ByteArrayNode();
		node.value = new byte[]{0, 1, -1, 127, -128};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Byte数组字段编码测试", json, "{\"value\":[0,1,-1,127,-128]}");

		ByteArrayNode newNode = DeepJson.parseObject(json, ByteArrayNode.class);
		AssertUtil.assertNotNull("Byte数组字段解码测试", newNode);
		AssertUtil.assertTrue("Byte数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldShortArray() {
		ShortArrayNode node = new ShortArrayNode();
		node.value = new short[]{0, 1, -1, 32767, -32768};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Short数组字段编码测试", json, "{\"value\":[0,1,-1,32767,-32768]}");

		ShortArrayNode newNode = DeepJson.parseObject(json, ShortArrayNode.class);
		AssertUtil.assertNotNull("Short数组字段解码测试", newNode);
		AssertUtil.assertTrue("Short数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldIntArray() {
		IntArrayNode node = new IntArrayNode();
		node.value = new int[]{0, 1, -1, 2147483647, -2147483648};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Int数组字段编码测试", json, "{\"value\":[0,1,-1,2147483647,-2147483648]}");

		IntArrayNode newNode = DeepJson.parseObject(json, IntArrayNode.class);
		AssertUtil.assertNotNull("Int数组字段解码测试", newNode);
		AssertUtil.assertTrue("Int数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldLongArray() {
		LongArrayNode node = new LongArrayNode();
		node.value = new long[]{0, 1, -1, 9223372036854775807L, -9223372036854775808L};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Long数组字段编码测试", json, "{\"value\":[0,1,-1,9223372036854775807,-9223372036854775808]}");

		LongArrayNode newNode = DeepJson.parseObject(json, LongArrayNode.class);
		AssertUtil.assertNotNull("Long数组字段解码测试", newNode);
		AssertUtil.assertTrue("Long数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldFloatArray() {
		FloatArrayNode node = new FloatArrayNode();
		node.value = new float[]{0, 1, -1, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.MIN_VALUE, Float.MAX_VALUE};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Float数组字段编码测试", json, "{\"value\":[0.0,1.0,-1.0,NaN,Infinity,-Infinity,1.4E-45,3.4028235E38]}");

		FloatArrayNode newNode = DeepJson.parseObject(json, FloatArrayNode.class);
		AssertUtil.assertNotNull("Float数组字段解码测试", newNode);
		AssertUtil.assertTrue("Float数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldDoubleArray() {
		DoubleArrayNode node = new DoubleArrayNode();
		node.value = new double[]{0, 1, -1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.MIN_VALUE, Double.MAX_VALUE};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Double数组字段编码测试", json, "{\"value\":[0.0,1.0,-1.0,NaN,Infinity,-Infinity,4.9E-324,1.7976931348623157E308]}");

		DoubleArrayNode newNode = DeepJson.parseObject(json, DoubleArrayNode.class);
		AssertUtil.assertNotNull("Double数组字段解码测试", newNode);
		AssertUtil.assertTrue("Double数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	@Test
	public void testFieldObjectArray() {
		ObjectArrayNode node = new ObjectArrayNode();
		node.value = new Node[]{new Node(1), new Node(2), new Node(3)};
		String json = DeepJson.toJsonString(node);
		AssertUtil.assertEquals("Object数组字段编码测试", json, "{\"value\":[{\"value\":1},{\"value\":2},{\"value\":3}]}");

		ObjectArrayNode newNode = DeepJson.parseObject(json, ObjectArrayNode.class);
		AssertUtil.assertNotNull("Object数组字段解码测试", newNode);
		AssertUtil.assertTrue("Object数组字段解码测试", Arrays.equals(node.value, newNode.value));
	}

	public static class Node {

		int value;

		public Node() {

		}

		public Node(int value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Node node = (Node) o;

			return value == node.value;
		}

		@Override
		public int hashCode() {
			return value;
		}
	}

	public static class BooleanArrayNode {

		boolean[] value;
	}

	public static class CharArrayNode {

		char[] value;
	}

	public static class ByteArrayNode {

		byte[] value;
	}

	public static class ShortArrayNode {

		short[] value;
	}

	public static class IntArrayNode {

		int[] value;
	}

	public static class LongArrayNode {

		long[] value;
	}

	public static class FloatArrayNode {

		float[] value;
	}

	public static class DoubleArrayNode {

		double[] value;
	}

	public static class ObjectArrayNode {

		Node[] value;
	}
}