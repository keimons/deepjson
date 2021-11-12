package com.keimons.deepjson.util;

/**
 * 平台相关工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class PlatformUtil {

	/**
	 * 获取Java版本
	 * <p>
	 * ...
	 * 1.6.x => 6
	 * 1.6.x => 7
	 * 1.8.x => 8
	 * 9.x   => 9
	 * 10.x  => 10
	 * 11.x  => 11
	 * ...
	 */
	private static final int JAVA_VERSION;

	static {
		String config = System.getProperty("java.specification.version", "1.6");
		final String[] components = config.split("\\.");
		final int[] version = new int[components.length];
		for (int i = 0; i < components.length; i++) {
			version[i] = Integer.parseInt(components[i]);
		}
		if (version[0] == 1) {
			JAVA_VERSION = version[1];
		} else {
			JAVA_VERSION = version[0];
		}
	}

	/**
	 * 获取Java版本
	 *
	 * @return Java版本
	 */
	public static int javaVersion() {
		return JAVA_VERSION;
	}

	/**
	 * 获取当前当前已使用内存的百分比
	 * <p>
	 * 通常情况下，在full gc之后调用，能获得更准去的内存使用情况
	 *
	 * @return 获取已使用内存的百分比近似值（0~100）
	 */
	public static int memoryUsage() {
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long maxMemory = Runtime.getRuntime().maxMemory();

		long usingMemory = totalMemory - freeMemory;
		return (int) (usingMemory * 100f / maxMemory);
	}
}