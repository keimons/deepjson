package com.keimons.deepjson.internal;

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
		@SuppressWarnings("rawtypes")
		AbstractJson.AbstractNode node = json.node;
		if (json.node == null) {
			node = AbstractJson.EMPTY_DICT;
		}
		return node.values;
	}
}