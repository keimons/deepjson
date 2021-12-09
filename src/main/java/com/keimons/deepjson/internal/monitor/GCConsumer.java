package com.keimons.deepjson.internal.monitor;

import java.util.List;

/**
 * 监视器消费者
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface GCConsumer {

	/**
	 * 消费函数
	 * <p>
	 * 当{@code Full GC}发生后，获取最新的内存占用，执行此消费函数。
	 *
	 * @param usage   内存使用（百分比）
	 * @param fgcTime 记录前x次{@code Full GC}的时间
	 */
	void accept(float usage, List<Long> fgcTime);
}