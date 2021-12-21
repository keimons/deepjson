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

	static final AbstractNode<?> EMPTY_LIST = new ListNode();

	static final AbstractNode<?> EMPTY_DICT = new DictNode();

	final Type type;

	AbstractNode<?> node; // must be not public

	public AbstractJson() {
		this.type = Type.AUTO;
	}

	public AbstractJson(Type type) {
		if (type == Type.LIST) {
			this.node = new ListNode();
		}
		if (type == Type.DICT) {
			this.node = new DictNode();
		}
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	protected AbstractJson(List<?> values) {
		this.type = Type.LIST;
		this.node = new ListNode((List<Object>) values);
	}

	@SuppressWarnings("unchecked")
	protected AbstractJson(Map<String, ?> values) {
		this.type = Type.DICT;
		this.node = new DictNode((Map<String, Object>) values);
	}

	AbstractNode<?> base() {
		if (type == Type.AUTO && (node == null || node.isEmpty())) {
			return EMPTY_DICT;
		}
		return node;
	}

	/**
	 * 获取内部节点
	 *
	 * @param def 默认值
	 * @return 内部节点
	 */
	private AbstractNode<?> base(AbstractNode<?> def) {
		if (type == Type.AUTO && (node == null || node.isEmpty())) {
			return def;
		}
		return node;
	}

	private AbstractNode<?> base(Type type) {
		// quickly
		if (node != null && node.type == type) {
			return node;
		}
		if (this.type == Type.AUTO && (node == null || node.isEmpty())) {
			if (type == Type.LIST) {
				node = new ListNode();
			} else {
				node = new DictNode();
			}
		}
		return node;
	}

	private void assertType(Type type, AbstractNode<?> node) {
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
		AbstractNode<?> node = base(Type.LIST);
		return node.type == Type.LIST;
	}

	/**
	 * 是否映射（如果当前容器为空，同样视作映射）
	 *
	 * @return 是否映射
	 */
	public boolean isDict() {
		AbstractNode<?> node = base(Type.DICT);
		return node.type == Type.DICT;
	}

	@Override
	public void add(Object value) {
		// empty dict convert to list
		AbstractNode<?> node = base(Type.LIST);
		assertType(Type.LIST, node);
		node.add(value);
	}

	@Override
	public void put(String key, Object value) {
		// empty list convert to dict
		AbstractNode<?> node = base(Type.DICT);
		assertType(Type.DICT, node);
		node.put(key, value);
	}

	public int size() {
		return node == null ? 0 : node.size();
	}

	@Override
	public Object get(int index) {
		AbstractNode<?> node = base(EMPTY_LIST);
		assertType(Type.LIST, node);
		return node.get(index);
	}

	@Override
	public Object get(String key) {
		AbstractNode<?> node = base(EMPTY_DICT);
		assertType(Type.DICT, node);
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
		// empty dict convert to list
		AbstractNode<?> node = base(EMPTY_LIST);
		assertType(Type.LIST, node);
		return node.iterator();
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		// empty list convert to dict
		AbstractNode<?> node = base(EMPTY_DICT);
		assertType(Type.DICT, node);
		return node.entrySet();
	}

	@Override
	public Object remove(int index) {
		AbstractNode<?> node = base(EMPTY_LIST);
		assertType(Type.LIST, node);
		return node.remove(index);
	}

	@Override
	public boolean remove(Object value) {
		AbstractNode<?> node = base(EMPTY_LIST);
		assertType(Type.LIST, node);
		return node.remove(value);
	}

	@Override
	public Object removeKey(String key) {
		AbstractNode<?> node = base(EMPTY_DICT);
		assertType(Type.DICT, node);
		return node.removeKey(key);
	}

	@Override
	public void clear() {
		if (node != null) {
			node.clear();
		}
	}

	@Override
	public int hashCode() {
		if (node == null) {
			return 0;
		}
		return node.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Object values;
		if (obj instanceof AbstractJson) {
			AbstractNode<?> node = ((AbstractJson) obj).node;
			if (this.node == null && node == null) {
				return true;
			}
			values = node.values;
		} else {
			values = obj;
		}
		if (this.node == null) {
			return EMPTY_DICT.equals(values) || EMPTY_LIST.equals(values);
		} else {
			return this.node.equals(values);
		}
	}

	public enum Type {
		AUTO, LIST, DICT
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

		public abstract boolean isEmpty();

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

		@Override
		public abstract int hashCode();

		@Override
		public abstract boolean equals(Object o);

		@NotNull
		@Override
		public Iterator<Object> iterator() {
			throw new UnsupportedOperationException();
		}

		public Set<Map.Entry<String, Object>> entrySet() {
			throw new UnsupportedOperationException();
		}

		public Object remove(int index) {
			throw new UnsupportedOperationException();
		}

		public boolean remove(Object value) {
			throw new UnsupportedOperationException();
		}

		public Object removeKey(String key) {
			throw new UnsupportedOperationException();
		}

		public abstract void clear();
	}

	static class ListNode extends AbstractNode<List<Object>> implements Iterable<Object> {

		public ListNode() {
			super(Type.LIST, new ArrayList<Object>());
		}

		public ListNode(List<Object> values) {
			super(Type.LIST, values);
		}

		@Override
		public boolean isEmpty() {
			return values.isEmpty();
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
		public int hashCode() {
			return values.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof List) {
				List<?> list = (List<?>) o;
				return list.equals(o);
			}
			return false;
		}

		@NotNull
		@Override
		public Iterator<Object> iterator() {
			return values.iterator();
		}

		@Override
		public Object remove(int index) {
			return values.remove(index);
		}

		@Override
		public boolean remove(Object value) {
			return values.remove(value);
		}

		@Override
		public void clear() {
			values.clear();
		}
	}

	static class DictNode extends AbstractNode<Map<String, Object>> {

		public DictNode() {
			super(Type.DICT, new HashMap<String, Object>());
		}

		public DictNode(Map<String, Object> values) {
			super(Type.DICT, values);
		}

		@Override
		public boolean isEmpty() {
			return values.isEmpty();
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
		public int hashCode() {
			return values.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Map) {
				Map<?, ?> dict = (Map<?, ?>) o;
				return dict.equals(o);
			}
			return false;
		}

		@Override
		public Set<Map.Entry<String, Object>> entrySet() {
			return values.entrySet();
		}

		@Override
		public Object removeKey(String key) {
			return values.remove(key);
		}

		@Override
		public void clear() {
			values.clear();
		}
	}
}