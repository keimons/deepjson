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
}