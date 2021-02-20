package com.keimons.deepjson.test;

import com.alibaba.fastjson.JSONObject;

public class Tester {

	public static void main(String[] args) {
		int[] ints = null;
		System.out.println(JSONObject.toJSONString(ints));
	}
}