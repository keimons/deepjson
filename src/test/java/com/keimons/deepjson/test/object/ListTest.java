package com.keimons.deepjson.test.object;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.SerializerOptions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * List序列化
 *
 * @author monkey
 * @version 1.0
 * @since 1.7
 **/
public class ListTest {

	@Test
	public void test() {
		Set<ListNode> set = new HashSet<>();

		set.add(new ListNode());
		set.add(new ListNode());
		System.out.println(DeepJson.toJsonString(set, SerializerOptions.ForceClassName));
		System.out.println(JSONObject.toJSONString(set, SerializerFeature.WriteClassName));
		String str = "[{\"value\":[/*@type:java.util.LinkedList*/0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99]},{\"value\":[/*@type:java.util.LinkedList*/]}]";
		System.out.println(JSONObject.parseObject(str, ListNode[].class)[0].getValue().getClass());
		try {
			System.out.println(TypeUtils.getCollectionItemClass(ListNode.class.getDeclaredField("value").getGenericType()));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSerializer() throws IOException, ClassNotFoundException {
		Comparator<Integer> comparator = Comparator.comparingInt(item -> item);
		System.out.println(comparator.getClass());
		PriorityBlockingQueue<Integer> queue = new PriorityBlockingQueue<>(1, comparator);
		queue.comparator();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("result.obj"));
		out.writeObject(queue);
		out.close();
		ObjectInputStream oin = new ObjectInputStream(new FileInputStream("result.obj"));
		PriorityBlockingQueue<?> o = (PriorityBlockingQueue<?>) oin.readObject();
		System.out.println(o.comparator().getClass());
	}
}