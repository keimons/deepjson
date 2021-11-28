package com.keimons.deepjson.internal;

import com.keimons.deepjson.Json;
import com.keimons.deepjson.util.TypeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认{@code json}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class AbstractJson implements Json {

	private AbstractNode node;

	public void add(Object value) {
		node.add(value);
	}

	public void put(Object key, Object value) {
		node.put(key, value);
	}

	public Object get(int index) {
		if (node.type != Type.LIST) {
			throw new RuntimeException();
		}
		return node.get(index);
	}

	public Object get(String key) {
		if (node.type != Type.DICT) {
			throw new RuntimeException();
		}
		return node.get(key);
	}

	public boolean getBooleanValue(int index) {
		Boolean value = TypeUtil.caseToBoolean(node.get(index));
		return value != null && value;
	}

	public boolean getBooleanValue(Object key) {
		Boolean value = TypeUtil.caseToBoolean(node.get(key));
		return value != null && value;
	}

	public int getIntValue(int index) {
		Object value = node.get(index);
		return value == null ? 0 : TypeUtil.caseToInteger(value);
	}

	public int getIntValue(String key) {
		Object value = node.get(key);
		return value == null ? 0 : TypeUtil.caseToInteger(value);
	}

	public enum Type {
		LIST, DICT
	}

	public interface Node {

		Type getType();

		@Nullable String key();
	}

	private static abstract class AbstractNode implements Node {

		private final Type type;

		public AbstractNode(Type type) {
			this.type = type;
		}

		@Override
		public Type getType() {
			return type;
		}

		public Object add(Object value) {
			throw new UnsupportedOperationException();
		}

		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		public Object get(int index) {
			throw new UnsupportedOperationException();
		}

		public Object get(Object key) {
			throw new UnsupportedOperationException();
		}
	}

	private static class ListNode extends AbstractNode {

		List<Object> values = new ArrayList<Object>();

		public ListNode(Type type) {
			super(type);
		}

		@Override
		public Object add(Object value) {
			return values.add(value);
		}

		@Override
		public Object get(int index) {
			return values.get(index);
		}

		@Override
		public @Nullable String key() {
			return null;
		}
	}

	private static class MapNode extends AbstractNode {

		Map<Object, Object> values = new HashMap<Object, Object>();

		public MapNode(Type type) {
			super(type);
		}

		@Override
		public Object put(Object key, Object value) {
			return values.put(key, value);
		}

		@Override
		public Object get(Object key) {
			return values.get(key);
		}

		@Override
		public @Nullable String key() {
			return null;
		}
	}
}