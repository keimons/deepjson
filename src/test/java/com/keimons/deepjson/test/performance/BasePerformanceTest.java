package com.keimons.deepjson.test.performance;

/**
 * 性能测试
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public abstract class BasePerformanceTest {

	/**
	 * 使用相同的种子
	 */
	protected long speed = 123456789L;

	/**
	 * 一百万次测试
	 */
	protected int times = 100_0000;

	/**
	 * 循环20次
	 */
	protected int loop = 20;

	private long timesTime;

	private long loopTime;

	protected void startTimesTest() {
		timesTime = System.nanoTime();
	}

	protected void finishTimesTest(int i) {
		System.out.println("第 " + i + " 次测试，耗时：" + (System.nanoTime() - timesTime) / 1000000f);
	}

	protected void startLoopTest() {
		loopTime = System.nanoTime();
	}

	protected void finishLoopTest() {
		System.out.println("测试总计耗时：" + (System.nanoTime() - loopTime) / 1000000f);
	}
}