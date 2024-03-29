package com.keimons.deepjson.test.codec.collection;

import com.keimons.deepjson.CodecOptions;
import com.keimons.deepjson.DeepJson;
import com.keimons.deepjson.test.AssertUtils;
import com.keimons.deepjson.util.TypeUtil;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link Collection}编解码器测试
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CollectionTest {

	@Test
	public void testEmptyList() {
		String json = DeepJson.toJsonString(new ArrayList<String>());
		AssertUtils.assertEquals("空List编码测试", json, "[]");

		List<String> newList = DeepJson.parseObject(json, TypeUtil.makeType(List.class, String.class));
		AssertUtils.assertNotNull("空List解码测试", newList);
		AssertUtils.assertTrue("空List解码测试", newList.size() == 0);
	}

	@Test
	public void testEmptySet() {
		String json = DeepJson.toJsonString(new HashSet<String>());
		AssertUtils.assertEquals("空Set编码测试", json, "[]");

		Set<String> newSet = DeepJson.parseObject(json, TypeUtil.makeType(Set.class, String.class));
		AssertUtils.assertNotNull("空Set解码测试", newSet);
		AssertUtils.assertTrue("空Set解码测试", newSet.size() == 0);
	}

	@Test
	public void testFullList() {
		List<String> list = Arrays.asList("0", "1", "2", "3");
		String json = DeepJson.toJsonString(list);
		AssertUtils.assertEquals("List编码测试", "[\"0\",\"1\",\"2\",\"3\"]", json);

		List<String> newList0 = DeepJson.parseObject(json, TypeUtil.makeType(List.class, String.class));
		AssertUtils.assertEquals("List解码测试", list, newList0);

		List<Integer> newList1 = DeepJson.parseObject(json, TypeUtil.makeType(List.class, Integer.class));
		AssertUtils.assertEquals("List解码测试", Arrays.asList(0, 1, 2, 3), newList1);
	}

	@Test
	public void testFullSet() {
		Set<String> set = new HashSet<String>();
		set.add("0");
		set.add("1");
		set.add("2");
		set.add("3");
		String json = DeepJson.toJsonString(set);
		AssertUtils.assertEquals("Set编码测试", json, "[\"0\",\"1\",\"2\",\"3\"]");

		HashSet<String> newSet0 = DeepJson.parseObject(json, TypeUtil.makeType(Set.class, String.class));
		AssertUtils.assertEquals("Set解码测试", set, newSet0);

		HashSet<Integer> newSet1 = DeepJson.parseObject(json, TypeUtil.makeType(Set.class, Integer.class));
		AssertUtils.assertEquals("Set解码测试", Set.of(0, 1, 2, 3), newSet1);
	}

	@Test
	public void testEmptyFieldList() {
		ListNode node = new ListNode();
		node.value0 = new ArrayList<EmptyNode>();
		node.value1 = new ArrayList<EmptyNode>();
		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("空List字段编码测试", json, "{\"value0\":[],\"value1\":[]}");

		ListNode newNode = DeepJson.parseObject(json, ListNode.class);
		AssertUtils.assertEquals("空List字段解码测试", newNode, node);
		AssertUtils.assertNotNull("空List字段解码测试", newNode.value0);
		AssertUtils.assertNotNull("空List字段解码测试", newNode.value1);
		AssertUtils.assertTrue("空List字段解码测试", newNode.value0.size() == 0);
		AssertUtils.assertTrue("空List字段解码测试", newNode.value1.size() == 0);
	}

	@Test
	public void testEmptyFieldSet() {
		SetNode node = new SetNode();
		node.value0 = new HashSet<EmptyNode>();
		node.value1 = new HashSet<EmptyNode>();
		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("空Set字段编码测试", json, "{\"value0\":[],\"value1\":[]}");

		SetNode newNode = DeepJson.parseObject(json, SetNode.class);
		AssertUtils.assertEquals("空Set字段解码测试", newNode, node);
		AssertUtils.assertNotNull("空Set字段解码测试", newNode.value0);
		AssertUtils.assertNotNull("空Set字段解码测试", newNode.value1);
		AssertUtils.assertTrue("空Set字段解码测试", newNode.value0.size() == 0);
		AssertUtils.assertTrue("空Set字段解码测试", newNode.value1.size() == 0);
	}

	@Test
	public void testFullFieldList() {
		ListNode node = new ListNode();
		node.value0 = new ArrayList<EmptyNode>();
		node.value0.add(new EmptyNode(1));
		node.value0.add(new EmptyNode(2));
		node.value0.add(new EmptyNode(3));
		node.value1 = new ArrayList<EmptyNode>();
		node.value1.add(new EmptyNode(4));
		node.value1.add(new EmptyNode(5));
		node.value1.add(new EmptyNode(6));
		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("List字段编码测试", json, "{\"value0\":[{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":[{\"value\":4},{\"value\":5},{\"value\":6}]}");

		ListNode newNode = DeepJson.parseObject(json, ListNode.class);
		AssertUtils.assertEquals("List字段解码测试", newNode, node);
	}

	@Test
	public void testFullFieldSet() {
		SetNode node = new SetNode();
		node.value0 = new HashSet<EmptyNode>();
		node.value0.add(new EmptyNode(1));
		node.value0.add(new EmptyNode(2));
		node.value0.add(new EmptyNode(3));
		node.value1 = new HashSet<EmptyNode>();
		node.value1.add(new EmptyNode(4));
		node.value1.add(new EmptyNode(5));
		node.value1.add(new EmptyNode(6));
		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("Set字段编码测试", json, "{\"value0\":[{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":[{\"value\":4},{\"value\":5},{\"value\":6}]}");

		SetNode newNode = DeepJson.parseObject(json, SetNode.class);
		AssertUtils.assertEquals("Set字段解码测试", newNode, node);
	}

	@Test
	public void testIdFieldList() {
		List<EmptyNode> list = new ArrayList<EmptyNode>();
		list.add(new EmptyNode(1));
		list.add(new EmptyNode(2));
		list.add(new EmptyNode(3));
		ListNode node = new ListNode();
		node.value0 = list;
		node.value1 = list;

		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("List字段带ID编码测试", json, "{\"value0\":[\"@id:1\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":\"$id:1\"}");

		ListNode newNode = DeepJson.parseObject(json, ListNode.class);
		AssertUtils.assertEquals("List字段带ID解码测试", newNode, node);
	}

	@Test
	public void testIdFieldSet() {
		Set<EmptyNode> set = new HashSet<EmptyNode>();
		set.add(new EmptyNode(1));
		set.add(new EmptyNode(2));
		set.add(new EmptyNode(3));
		SetNode node = new SetNode();
		node.value0 = set;
		node.value1 = set;

		String json = DeepJson.toJsonString(node);
		AssertUtils.assertEquals("Set字段带ID编码测试", json, "{\"value0\":[\"@id:1\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":\"$id:1\"}");

		SetNode newNode = DeepJson.parseObject(json, SetNode.class);
		AssertUtils.assertEquals("Set字段带ID解码测试", newNode, node);
	}

	@Test
	public void testTypeFieldList() {
		ListNode node = new ListNode();
		node.value0 = new CopyOnWriteArrayList<EmptyNode>();
		node.value0.add(new EmptyNode(1));
		node.value0.add(new EmptyNode(2));
		node.value0.add(new EmptyNode(3));
		node.value1 = new CopyOnWriteArrayList<EmptyNode>();
		node.value1.add(new EmptyNode(4));
		node.value1.add(new EmptyNode(5));
		node.value1.add(new EmptyNode(6));

		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		AssertUtils.assertEquals("List字段带id编码测试", json, "{\"value0\":[\"$type:java.util.concurrent.CopyOnWriteArrayList\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":[\"$type:java.util.concurrent.CopyOnWriteArrayList\",{\"value\":4},{\"value\":5},{\"value\":6}]}");

		ListNode newNode = DeepJson.parseObject(json, ListNode.class);
		AssertUtils.assertEquals("List字段带id解码测试", newNode, node);
	}

	@Test
	public void testTypeFieldSet() {
		SetNode node = new SetNode();
		node.value0 = new LinkedHashSet<EmptyNode>();
		node.value0.add(new EmptyNode(1));
		node.value0.add(new EmptyNode(2));
		node.value0.add(new EmptyNode(3));
		node.value1 = new LinkedHashSet<EmptyNode>();
		node.value1.add(new EmptyNode(4));
		node.value1.add(new EmptyNode(5));
		node.value1.add(new EmptyNode(6));

		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		AssertUtils.assertEquals("Set字段带type编码测试", json, "{\"value0\":[\"$type:java.util.LinkedHashSet\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":[\"$type:java.util.LinkedHashSet\",{\"value\":4},{\"value\":5},{\"value\":6}]}");

		SetNode newNode = DeepJson.parseObject(json, SetNode.class);
		AssertUtils.assertEquals("Set字段带type解码测试", newNode, node);
	}

	@Test
	public void testTypeIdFieldList() {
		List<EmptyNode> list = new CopyOnWriteArrayList<EmptyNode>();
		list.add(new EmptyNode(1));
		list.add(new EmptyNode(2));
		list.add(new EmptyNode(3));
		ListNode node = new ListNode();
		node.value0 = list;
		node.value1 = list;

		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		AssertUtils.assertEquals("List字段带type和id编码测试", json, "{\"value0\":[\"$type:java.util.concurrent.CopyOnWriteArrayList,@id:1\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":\"$id:1\"}");

		ListNode newNode = DeepJson.parseObject(json, ListNode.class);
		AssertUtils.assertEquals("List字段带type和id解码测试", newNode, node);
	}

	@Test
	public void testTypeIdFieldSet() {
		Set<EmptyNode> set = new LinkedHashSet<EmptyNode>();
		set.add(new EmptyNode(1));
		set.add(new EmptyNode(2));
		set.add(new EmptyNode(3));
		SetNode node = new SetNode();
		node.value0 = set;
		node.value1 = set;

		String json = DeepJson.toJsonString(node, CodecOptions.WriteClassName);
		AssertUtils.assertEquals("Set字段带type和id编码测试", json, "{\"value0\":[\"$type:java.util.LinkedHashSet,@id:1\",{\"value\":1},{\"value\":2},{\"value\":3}],\"value1\":\"$id:1\"}");

		SetNode newNode = DeepJson.parseObject(json, SetNode.class);
		AssertUtils.assertEquals("Set字段带type和id解码测试", newNode, node);
	}

	public static class EmptyNode {

		int value;

		EmptyNode() {

		}

		EmptyNode(int value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof EmptyNode) {
				EmptyNode other = (EmptyNode) obj;
				return value == other.value;
			}
			return false;
		}
	}

	public static class ListNode {

		List<EmptyNode> value0;

		List<EmptyNode> value1;

		@Override
		public boolean equals(Object o) {
			if (o instanceof ListNode) {
				ListNode other = (ListNode) o;
				return Objects.equals(value0, other.value0) && Objects.equals(value1, other.value1);
			}
			return false;
		}
	}

	public static class SetNode {

		Set<EmptyNode> value0;

		Set<EmptyNode> value1;

		@Override
		public boolean equals(Object o) {
			if (o instanceof SetNode) {
				SetNode other = (SetNode) o;
				return Objects.equals(value0, other.value0) && Objects.equals(value1, other.value1);
			}
			return false;
		}
	}
}