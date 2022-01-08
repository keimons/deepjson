package com.keimons.deepjson.test;

/**
 * 断言工具
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see AssertionError 断言错误
 * @since 1.6
 */
public class AssertUtils {

	private AssertUtils() {
	}

	/**
	 * 断言条件为真
	 * <p>
	 * 如果为假，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message   {@link AssertionError}错误信息
	 * @param condition 检测条件
	 */
	public static void assertTrue(String message, boolean condition) {
		if (!condition) {
			fail(message);
		}
	}

	/**
	 * 断言条件为假
	 * <p>
	 * 如果为真，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message   {@link AssertionError}错误信息
	 * @param condition 检测条件
	 */
	public static void assertFalse(String message, boolean condition) {
		assertTrue(message, !condition);
	}

	/**
	 * 断言两个对象相等
	 * <p>
	 * 如果不相等，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message  {@link AssertionError}错误信息
	 * @param expected 预期值
	 * @param actual   实际值
	 */
	static public void assertEquals(String message, Object expected, Object actual) {
		if (!isEquals(expected, actual)) {
			failNotEquals(message, expected, actual);
		}
	}

	/**
	 * 断言两个对象不相等。
	 * <p>
	 * 如果相等，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message    {@link AssertionError}错误信息
	 * @param unexpected 意外值
	 * @param actual     实际值
	 */
	public static void assertNotEquals(String message, Object unexpected, Object actual) {
		if (isEquals(unexpected, actual)) {
			failEquals(message, actual);
		}
	}

	/**
	 * 断言对象为空
	 * <p>
	 * 如果对象不为空，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message {@link AssertionError}错误信息
	 * @param object  要检查的对象
	 */
	public static void assertNull(String message, Object object) {
		if (object == null) {
			return;
		}
		failNotNull(message, object);
	}

	/**
	 * 断言对象不为空
	 * <p>
	 * 如果对象为空，则抛出带有指定消息的{@link AssertionError}错误。
	 *
	 * @param message {@link AssertionError}错误信息
	 * @param object  要检查的对象
	 */
	public static void assertNotNull(String message, Object object) {
		assertTrue(message, object != null);
	}

	private static void fail(String message) {
		throw new AssertionError(message);
	}

	private static boolean isEquals(Object expected, Object actual) {
		if (expected == null) {
			return actual == null;
		}
		return expected.equals(actual);
	}

	private static void failEquals(String message, Object actual) {
		String formatted = "Values should be different. ";
		if (message != null) {
			formatted = message + ". ";
		}

		formatted += "Actual: " + actual;
		fail(formatted);
	}

	private static void failNotNull(String message, Object actual) {
		String formatted = "";
		if (message != null) {
			formatted = message + " ";
		}
		fail(formatted + "expected null, but was:<" + actual + ">");
	}

	private static void failNotEquals(String message, Object expected,
									  Object actual) {
		fail(format(message, expected, actual));
	}

	static String format(String message, Object expected, Object actual) {
		String formatted = "";
		if (message != null && !message.equals("")) {
			formatted = message + " ";
		}
		String expectedString = String.valueOf(expected);
		String actualString = String.valueOf(actual);
		if (expectedString.equals(actualString)) {
			return formatted + "expected: "
					+ formatClassAndValue(expected, expectedString)
					+ " but was: " + formatClassAndValue(actual, actualString);
		} else {
			return formatted + "expected:<" + expectedString + "> but was:<"
					+ actualString + ">";
		}
	}

	static String formatClassAndValue(Object value, String valueString) {
		String className = value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}
}