package com.keimons.deepjson.test.codec;

import com.keimons.deepjson.DeepJson;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 基础类型数组编解码测试
 * <p>
 * {@code boolean[], byte[], char[], short[], int[], long[], float[], double[]}类型
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class PrimitiveArrayCodecTest {

	@Test
	public void testDecode() throws ClassNotFoundException {
		String zj1 = "[true, false, \"true\", \"false\"]";
		String zj2 = "{\"$type\":\"[Z\",\"$value\":[true, false, \"true\", \"false\"]}";
		boolean[] zav1 = DeepJson.parseObject(zj1, boolean[].class);
		boolean[] zav2 = DeepJson.parseObject(zj2, boolean[].class);
		System.out.println("boolean array: " + Arrays.toString(zav1));
		System.out.println("boolean array: " + Arrays.toString(zav2));

		String bj1 = "[1, 20, \"127\", \"-128\"]";
		String bj2 = "{\"$type\":\"[B\",\"$value\":[1, 20, \"127\", \"-128\"]}";
		byte[] bav1 = DeepJson.parseObject(bj1, byte[].class);
		byte[] bav2 = DeepJson.parseObject(bj2, byte[].class);
		System.out.println("byte array: " + Arrays.toString(bav1));
		System.out.println("byte array: " + Arrays.toString(bav2));

		String cj1 = "[\"a\", \"\0\", \"\u0078\", \"\u0099\"]";
		String cj2 = "{\"$type\":\"[C\",\"$value\":[\"a\", \"\0\", \"\u0078\", \"\u0099\"]}";
		char[] cav1 = DeepJson.parseObject(cj1, char[].class);
		char[] cav2 = DeepJson.parseObject(cj2, char[].class);
		System.out.println("char array: " + Arrays.toString(cav1));
		System.out.println("char array: " + Arrays.toString(cav2));

		String sj1 = "[1, 20, \"127\", \"-128\"]";
		String sj2 = "{\"$type\":\"[S\",\"$value\":[1, 20, \"127\", \"-128\"]}";
		short[] sav1 = DeepJson.parseObject(sj1, short[].class);
		short[] sav2 = DeepJson.parseObject(sj2, short[].class);
		System.out.println("short array: " + Arrays.toString(sav1));
		System.out.println("short array: " + Arrays.toString(sav2));

		String ij1 = "[1, 20, \"127\", \"-128\"]";
		String ij2 = "{\"$type\":\"[I\",\"$value\":[1, 20, \"127\", \"-128\"]}";
		int[] iav1 = DeepJson.parseObject(ij1, int[].class);
		int[] iav2 = DeepJson.parseObject(ij2, int[].class);
		System.out.println("int array: " + Arrays.toString(iav1));
		System.out.println("int array: " + Arrays.toString(iav2));

		String lj1 = "[1L, 20l, \"127\", \"-128\"]";
		String lj2 = "{\"$type\":\"[J\",\"$value\":[1, 20, \"127L\", \"-128l\"]}";
		long[] lav1 = DeepJson.parseObject(lj1, long[].class);
		long[] lav2 = DeepJson.parseObject(lj2, long[].class);
		System.out.println("long array: " + Arrays.toString(lav1));
		System.out.println("long array: " + Arrays.toString(lav2));

		String fj1 = "[1.22e8f, 1.22e-4f, \"-1.23e12\", \"-1.23e-16F\"]";
		String fj2 = "{\"$type\":\"[F\",\"$value\":[1.22e8f, 1.22e-4f, \"-1.23e12\", \"-1.23e-16F\"]}";
		float[] fav1 = DeepJson.parseObject(fj1, float[].class);
		float[] fav2 = DeepJson.parseObject(fj2, float[].class);
		System.out.println("float array: " + Arrays.toString(fav1));
		System.out.println("float array: " + Arrays.toString(fav2));

		String dj1 = "[1.22e8d, 1.22e-4d, \"-1.23e12\", \"-1.23e-16D\"]";
		String dj2 = "{\"$type\":\"[D\",\"$value\":[1.22e8d, 1.22e-4d, \"-1.23e12\", \"-1.23e-16D\"]}";
		double[] dav1 = DeepJson.parseObject(dj1, double[].class);
		double[] dav2 = DeepJson.parseObject(dj2, double[].class);
		System.out.println("double array: " + Arrays.toString(dav1));
		System.out.println("double array: " + Arrays.toString(dav2));
	}
}