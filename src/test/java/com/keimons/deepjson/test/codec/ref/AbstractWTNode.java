package com.keimons.deepjson.test.codec.ref;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试节点
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AbstractWTNode<T> {

	public List<? extends Integer> value0;

	public List<? super Integer> value1 = new ArrayList<>();

	public List<? extends T> value2;

	public List<? super T> value3 = new ArrayList<>();
}