package com.keimons.deepjson.support.codec;

import com.keimons.deepjson.*;
import com.keimons.deepjson.support.ElementsFuture;
import com.keimons.deepjson.support.IncompatibleTypeException;
import com.keimons.deepjson.support.InstantiationFailedException;
import com.keimons.deepjson.util.TypeNotFoundException;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * {@link Collection}编解码器
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CollectionCodec extends AbstractOnlineCodec<Collection<?>> {

	public static final CollectionCodec instance = new CollectionCodec();

	public static final Map<Class<?>, Class<?>> interfaces = new HashMap<Class<?>, Class<?>>();

	static {
		interfaces.put(Collection.class, ArrayList.class);
		interfaces.put(List.class, ArrayList.class);
		interfaces.put(Queue.class, LinkedList.class);
		interfaces.put(Deque.class, LinkedList.class);
		interfaces.put(BlockingQueue.class, LinkedBlockingQueue.class);
		interfaces.put(BlockingDeque.class, LinkedBlockingDeque.class);
		interfaces.put(Set.class, HashSet.class);
		interfaces.put(SortedSet.class, TreeSet.class);
		interfaces.put(NavigableSet.class, TreeSet.class);
	}

	@Override
	public void build(WriterContext context, Collection<?> value) {
		ElementsFuture future = new ElementsFuture();
		context.cache(future, EmptyCodec.instance);
		for (Object obj : value) {
			context.build(obj);
			future.addCount();
		}
	}

	@Override
	public void encode(WriterContext context, JsonWriter writer, CodecModel model, Collection<?> value, int uniqueId, long options) throws IOException {
		char mark = '[';
		// write class name
		String info = null;
		if (CodecOptions.WriteClassName.isOptions(options)) {
			Class<?> clazz = value.getClass();
			if (CodecConfig.WHITE_COLLECTION.contains(clazz)) {
				info = "$type:" + clazz.getName();
			}
		}
		if (uniqueId >= 0) {
			if (info == null) {
				info = "@id:" + uniqueId;
			} else {
				info += ",@id:" + uniqueId;
			}
		}
		if (info != null) {
			writer.writeMark(mark);
			writer.writeWithQuote(info);
			mark = ',';
		}
		mark = encode(context, writer, value, options, mark);
		if (mark == '[') {
			writer.writeMark('[');
		}
		writer.writeMark(']');
	}

	protected char encode(WriterContext context, JsonWriter writer, Collection<?> value, long options, char mark) throws IOException {
		Object future = context.poll();
		if (!(future instanceof ElementsFuture)) {
			throw new RuntimeException("deep json bug");
		}
		int count = ((ElementsFuture) future).getCount();
		for (int i = 0; i < count; i++) {
			writer.writeMark(mark);
			context.encode(writer, CodecModel.V, options);
			mark = ',';
		}
		return mark;
	}

	@Override
	public Collection<?> decode(ReaderContext context, ReaderBuffer buf, Class<?> clazz, long options) {
		buf.assertExpectedSyntax(SyntaxToken.LBRACKET); // 预期当前语法是 "["
		Type et = context.findType(Collection.class, "E");
		SyntaxToken token;
		Collection<Object> instance = null;
		int[] hooks = null;
		int count = 0;
		for (; ; ) {
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			if (token == SyntaxToken.STRING && buf.check$Type()) { // 检测是否类型
				String typeName = buf.get$Type();
				Class<?> instanceType;
				try {
					instanceType = Class.forName(typeName);
				} catch (ClassNotFoundException e) {
					throw new TypeNotFoundException(e);
				}
				if (!Collection.class.isAssignableFrom(instanceType)) { // 必须是 集合类型 或 子类
					throw new IncompatibleTypeException(instanceType, Collection.class);
				}
				if (!clazz.isAssignableFrom(instanceType)) {
					throw new IncompatibleTypeException(instanceType, clazz);
				}
				clazz = instanceType;
				instance = createInstance(clazz, et);

				if (buf.checkAtId()) {
					context.put(buf.getAtId(), instance);
				}
			} else if (token == SyntaxToken.STRING && buf.checkAtId()) { // 检测是否设置ID
				if (instance == null) {
					instance = createInstance(clazz, et);
				}
				context.put(buf.getAtId(), instance);
			} else if (token == SyntaxToken.STRING && buf.check$Id()) {
				if (hooks == null) {
					hooks = new int[16]; // 准备8个引用
				}
				if (count >= hooks.length) {
					hooks = Arrays.copyOf(hooks, hooks.length << 1);
				}
				if (instance == null) {
					instance = createInstance(clazz, et);
				}
				int index = (count >> 1) + instance.size(); // 用于记录位置
				hooks[count++] = index;
				hooks[count++] = buf.get$Id();
			} else {
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
				if (instance == null) {
					instance = createInstance(clazz, et);
				}
				instance.add(context.decode(buf, et, options));
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}
		if (instance == null) {
			instance = createInstance(clazz, et);
		}
		if (hooks != null) {
			Collection<Object> fi = instance;
			for (int i = 0; i < count; i += 2) {
				final int index = hooks[i];
				final int unique = hooks[i + 1];
				context.addCompleteHook(new Runnable() {
					@Override
					public void run() {
						List<Object> list = new ArrayList<Object>(fi);
						fi.clear();
						fi.addAll(list.subList(0, index));
						fi.add(context.get(unique));
						fi.addAll(list.subList(index, list.size()));
					}
				});
			}
		}
		return instance;
	}

	/*
	private void decode0(final Collection<Object> instance, final ReaderContext context, ReaderBuffer buf, Type et, long options) {
		int[] hooks = null;
		int count = 0;
		for (; ; ) {
			SyntaxToken token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			if (token == SyntaxToken.STRING && buf.check$Id()) {
				if (hooks == null) {
					hooks = new int[16]; // 准备8个引用
				}
				if (count >= hooks.length) {
					hooks = Arrays.copyOf(hooks, hooks.length << 1);
				}
				int index = (count >> 1) + instance.size(); // 用于记录位置
				hooks[count++] = index;
				hooks[count++] = buf.get$Id();
			} else if (token == SyntaxToken.STRING && buf.check$Type()) {

			} else {
				buf.assertExpectedSyntax(SyntaxToken.OBJECTS);
				instance.add(context.decode(buf, et, options));
			}
			token = buf.nextToken();
			if (token == SyntaxToken.RBRACKET) {
				break;
			}
			buf.assertExpectedSyntax(SyntaxToken.COMMA);
		}

		if (hooks != null) {
			for (int i = 0; i < count; i += 2) {
				final int index = hooks[i];
				final int unique = hooks[i + 1];
				context.addCompleteHook(new Runnable() {
					@Override
					public void run() {
						List<Object> list = new ArrayList<Object>(instance);
						instance.clear();
						instance.addAll(list.subList(0, index));
						instance.add(context.get(unique));
						instance.addAll(list.subList(index, list.size()));
					}
				});
			}
		}
	}
	*/

	/**
	 * 创建一个{@link Collection}实例
	 * <p>
	 * 关于{@code type}的类型，一定不是
	 *
	 * @param clazz 集合类型
	 * @param et    集合中元素类型
	 * @return 集合实例
	 */
	@SuppressWarnings("unchecked")
	private Collection<Object> createInstance(Class<?> clazz, Type et) {
		if (clazz.isInterface()) {
			return createInterface(clazz);
		}
		if (EnumSet.class.isAssignableFrom(clazz)) {
			return createEnum(et);
		}
		if (Modifier.isAbstract(clazz.getModifiers())) {
			return createAbstract(clazz);
		}
		try {
			return (Collection<Object>) clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new InstantiationFailedException("create instance error, class " + clazz.getName());
		}
	}

	/**
	 * 根据接口创建一个{@link Collection}实例
	 *
	 * @param clazz 接口
	 * @return {@link Collection}实例
	 */
	@SuppressWarnings("unchecked")
	private Collection<Object> createInterface(Class<?> clazz) {
		Class<?> target = interfaces.get(clazz);
		if (target == null) {
			throw new InstantiationFailedException("create instance error, interface " + clazz.getName());
		} else {
			try {
				// always successful
				return (Collection<Object>) target.getConstructor().newInstance();
			} catch (Exception e) {
				throw new InstantiationFailedException("create instance error, class " + target.getName(), e);
			}
		}
	}

	/**
	 * 根据抽象类创建一个{@link EnumSet}实例
	 *
	 * @param et {@link EnumSet}中{@code K}的类型
	 * @return {@link EnumSet}实例
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private Collection<Object> createEnum(Type et) {
		if (!(et instanceof Class) || !((Class<?>) et).isEnum()) {
			throw new InstantiationFailedException("unrecognized enum set of type: " + et);
		}
		// 抑制警告 已经确认过 "K" 的类型是enum。
		return EnumSet.noneOf((Class<? extends Enum>) et);
	}

	/**
	 * 根据抽象类创建一个{@link Collection}实例
	 *
	 * @param clazz 抽象类
	 * @return {@link Collection}实例
	 */
	private Collection<Object> createAbstract(Class<?> clazz) {
		// 犀利
		if (AbstractCollection.class == clazz || AbstractList.class == clazz) {
			return new ArrayList<Object>();
		} else if (AbstractQueue.class == clazz) {
			return new LinkedList<Object>();
		} else if (AbstractSet.class == clazz) {
			return new HashSet<Object>();
		} else {
			throw new InstantiationFailedException("unrecognized abstract of type: " + clazz.getName());
		}
	}
}