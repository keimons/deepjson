package com.keimons.deepjson.memory;

import java.util.List;

/**
 * 消费者
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public interface MonitorConsumer {

	/**
	 * 回调函数
	 *
	 * @param usage 内存使用
	 * @param gcs   GC时间
	 */
	void accept(float usage, List<Long> gcs);
}