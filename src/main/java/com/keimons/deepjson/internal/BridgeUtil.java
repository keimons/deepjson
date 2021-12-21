package com.keimons.deepjson.internal;

import java.util.List;
import java.util.Map;

/**
 * 桥接工具（部分方法通过桥接工具访问）
 * <p>
 * 注意：桥接工具可能会改变！
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class BridgeUtil {

	public static Object getValues(AbstractJson json) {
		AbstractJson.AbstractNode<?> node = json.base();
		return node.values;
	}

	public static void putValues(AbstractJson json, List<Object> values) {
		json.node = new AbstractJson.ListNode(values);
	}

	public static void putValues(AbstractJson json, Map<String, Object> values) {
		json.node = new AbstractJson.DictNode(values);
	}
}