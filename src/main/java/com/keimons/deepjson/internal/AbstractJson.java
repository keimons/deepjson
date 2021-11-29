package com.keimons.deepjson.internal;

import com.keimons.deepjson.util.TypeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 默认{@code json}实现
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public abstract class AbstractJson implements Json {

	@SuppressWarnings("rawtypes")
	static final AbstractNode<?> EMPTY_LIST = new ListNode();

	@SuppressWarnings("rawtypes")
	static final AbstractNode<?> EMPTY_DICT = new DictNode();

	@SuppressWarnings("rawtypes")
	AbstractNode node; // must be private

	public AbstractJson() {

	}

	protected AbstractJson(List<Object> values) {
		this.node = new ListNode(values);
	}

	protected AbstractJson(Map<String, Object> values) {
		this.node = new DictNode(values);
	}

	private void assertType(Type type) {
		if (node.type != type) {
			throw new RuntimeException();
		}
	}

	/**
	 * 是否数组（如果当前容器为空，同样视作数组）
	 *
	 * @return 是否数组
	 */
	public boolean isArray() {
		return node == null || node.type == Type.LIST || node.size() == 0;
	}

	/**
	 * 是否映射（如果当前容器为空，同样视作映射）
	 *
	 * @return 是否映射
	 */
	public boolean isDict() {
		return node == null || node.type == Type.DICT || node.size() == 0;
	}

	@Override
	public void add(Object value) {
		// empty map convert to list
		if (node == null || (node.type == Type.DICT && node.size() == 0)) {
			node = new ListNode();
		}
		assertType(Type.LIST);
		node.add(value);
	}

	@Override
	public void put(String key, Object value) {
		// empty list convert to map
		if (node == null || (node.type == Type.LIST && node.size() == 0)) {
			node = new DictNode();
		}
		assertType(Type.DICT);
		node.put(key, value);
	}

	public int size() {
		return node == null ? 0 : node.size();
	}

	@Override
	public Object get(int index) {
		AbstractNode<?> node = this.node == null ? EMPTY_LIST : this.node;
		assertType(Type.LIST);
		return node.get(index);
	}

	@Override
	public Object get(String key) {
		AbstractNode<?> node = this.node == null ? EMPTY_DICT : this.node;
		assertType(Type.DICT);
		return node.get(key);
	}

	@Override
	public boolean getBooleanValue(int index) {
		Object value = get(index);
		Boolean b = TypeUtil.caseToBoolean(value);
		return b != null && b;
	}

	@Override
	public boolean getBooleanValue(String key) {
		Object value = get(key);
		Boolean b = TypeUtil.caseToBoolean(value);
		return b != null && b;
	}

	@Override
	public Boolean getBoolean(int index) {
		Object value = get(index);
		return TypeUtil.caseToBoolean(value);
	}

	@Override
	public Boolean getBoolean(String key) {
		Object value = get(key);
		return TypeUtil.caseToBoolean(value);
	}

	@Override
	public char getCharValue(int index) {
		Object value = get(index);
		Character c = TypeUtil.caseToCharacter(value);
		return c == null ? (char) 0 : c;
	}

	@Override
	public char getCharValue(String key) {
		Object value = get(key);
		Character c = TypeUtil.caseToCharacter(value);
		return c == null ? (char) 0 : c;
	}

	@Override
	public Character getCharacter(int index) {
		Object value = get(index);
		return TypeUtil.caseToCharacter(value);
	}

	@Override
	public Character getCharacter(String key) {
		Object value = get(key);
		return TypeUtil.caseToCharacter(value);
	}

	@Override
	public byte getByteValue(int index) {
		Object value = get(index);
		Byte i = TypeUtil.caseToByte(value);
		return i == null ? 0 : i;
	}

	@Override
	public byte getByteValue(String key) {
		Object value = get(key);
		Byte i = TypeUtil.caseToByte(value);
		return i == null ? 0 : i;
	}

	@Override
	public Byte getByte(int index) {
		Object value = get(index);
		return TypeUtil.caseToByte(value);
	}

	@Override
	public Byte getByte(String key) {
		Object value = get(key);
		return TypeUtil.caseToByte(value);
	}

	@Override
	public short getShortValue(int index) {
		Object value = get(index);
		Short i = TypeUtil.caseToShort(value);
		return i == null ? 0 : i;
	}

	@Override
	public short getShortValue(String key) {
		Object value = get(key);
		Short i = TypeUtil.caseToShort(value);
		return i == null ? 0 : i;
	}

	@Override
	public Short getShort(int index) {
		Object value = get(index);
		return TypeUtil.caseToShort(value);
	}

	@Override
	public Short getShort(String key) {
		Object value = get(key);
		return TypeUtil.caseToShort(value);
	}

	@Override
	public int getIntValue(int index) {
		Object value = get(index);
		Integer i = TypeUtil.caseToInteger(value);
		return i == null ? 0 : i;
	}

	@Override
	public int getIntValue(String key) {
		Object value = get(key);
		Integer i = TypeUtil.caseToInteger(value);
		return i == null ? 0 : i;
	}

	@Override
	public Integer getInteger(int index) {
		Object value = get(index);
		return TypeUtil.caseToInteger(value);
	}

	@Override
	public Integer getInteger(String key) {
		Object value = get(key);
		return TypeUtil.caseToInteger(value);
	}

	@Override
	public long getLongValue(int index) {
		Object value = get(index);
		Long l = TypeUtil.caseToLong(value);
		return l == null ? 0L : l;
	}

	@Override
	public long getLongValue(String key) {
		Object value = get(key);
		Long l = TypeUtil.caseToLong(value);
		return l == null ? 0L : l;
	}

	@Override
	public Long getLong(int index) {
		Object value = get(index);
		return TypeUtil.caseToLong(value);
	}

	@Override
	public Long getLong(String key) {
		Object value = get(key);
		return TypeUtil.caseToLong(value);
	}

	@Override
	public float getFloatValue(int index) {
		Object value = get(index);
		Float f = TypeUtil.caseToFloat(value);
		return f == null ? 0F : f;
	}

	@Override
	public float getFloatValue(String key) {
		Object value = get(key);
		Float f = TypeUtil.caseToFloat(value);
		return f == null ? 0F : f;
	}

	@Override
	public Float getFloat(int index) {
		Object value = get(index);
		return TypeUtil.caseToFloat(value);
	}

	@Override
	public Float getFloat(String key) {
		Object value = get(key);
		return TypeUtil.caseToFloat(value);
	}

	@Override
	public double getDoubleValue(int index) {
		Object value = get(index);
		Double d = TypeUtil.caseToDouble(value);
		return d == null ? 0D : d;
	}

	@Override
	public double getDoubleValue(String key) {
		Object value = get(key);
		Double d = TypeUtil.caseToDouble(value);
		return d == null ? 0D : d;
	}

	@Override
	public Double getDouble(int index) {
		Object value = get(index);
		return TypeUtil.caseToDouble(value);
	}

	@Override
	public Double getDouble(String key) {
		Object value = get(key);
		return TypeUtil.caseToDouble(value);
	}

	@Override
	public Iterator<Object> iterator() {
		// empty list convert to map
		if (node == null || node.size() == 0) {
			return EMPTY_LIST.iterator();
		} else {
			assertType(Type.LIST);
			return node.iterator();
		}
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		if (node == null || node.size() == 0) {
			return EMPTY_DICT.entrySet();
		} else {
			assertType(Type.DICT);
			return node.entrySet();
		}
	}

	@Override
	public void clear() {
		this.node.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (node == null || node.size() == 0) {
			return EMPTY_DICT.values.equals(obj) || EMPTY_LIST.values.equals(obj);
		}
		return node.values.equals(obj);
	}

	public enum Type {
		LIST, DICT
	}

	public interface Node {

		@Nullable String key();
	}

	static abstract class AbstractNode<T> implements Iterable<Object> {

		final Type type;

		T values;

		public AbstractNode(Type type, T values) {
			this.type = type;
			this.values = values;
		}

		public abstract int size();

		public void add(Object value) {
			throw new UnsupportedOperationException();
		}

		public void put(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		public Object get(int index) {
			throw new UnsupportedOperationException();
		}

		public Object get(String key) {
			throw new UnsupportedOperationException();
		}

		@NotNull
		@Override
		public Iterator<Object> iterator() {
			throw new UnsupportedOperationException();
		}

		public Set<Map.Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		public abstract void clear();
	}

	private static class ListNode extends AbstractNode<List<Object>> implements Iterable<Object> {

		public ListNode() {
			super(Type.LIST, new ArrayList<Object>());
		}

		public ListNode(List<Object> values) {
			super(Type.LIST, values);
		}

		@Override
		public int size() {
			return values.size();
		}

		@Override
		public void add(Object value) {
			values.add(value);
		}

		@Override
		public Object get(int index) {
			return values.get(index);
		}

		@Override
		public void clear() {
			values.clear();
		}

		@NotNull
		@Override
		public Iterator<Object> iterator() {
			return values.iterator();
		}
	}

	private static class DictNode extends AbstractNode<Map<String, Object>> {

		public DictNode() {
			super(Type.DICT, new HashMap<String, Object>());
		}

		public DictNode(Map<String, Object> values) {
			super(Type.LIST, values);
		}

		@Override
		public int size() {
			return values.size();
		}

		@Override
		public void put(String key, Object value) {
			values.put(key, value);
		}

		@Override
		public Object get(String key) {
			return values.get(key);
		}

		@Override
		public Set<Map.Entry<String, Object>> entrySet() {
			return values.entrySet();
		}

		@Override
		public void clear() {
			values.clear();
		}
	}
}