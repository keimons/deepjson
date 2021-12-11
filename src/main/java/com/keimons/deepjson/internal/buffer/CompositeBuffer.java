package com.keimons.deepjson.internal.buffer;

import com.keimons.deepjson.Buffer;
import com.keimons.deepjson.CodecConfig;
import com.keimons.deepjson.Generator;
import com.keimons.deepjson.util.PlatformUtil;
import com.keimons.deepjson.util.RingQueue;
import com.keimons.deepjson.util.SimpleReference;
import com.keimons.deepjson.util.WriteFailedException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;

/**
 * 复合缓冲区
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class CompositeBuffer implements Buffer {

	/**
	 * 所有组件，目标是4M
	 */
	private Component[] components = new Component[16];

	/**
	 * 复合缓冲区，目标是4M
	 */
	private char[][] buffers = new char[16][];

	/**
	 * 写入缓冲区
	 */
	private int bufferIndex;

	/**
	 * 当前正在写入的缓冲区
	 */
	private char[] buf;

	/**
	 * 写入位置
	 */
	private int writerIndex;

	/**
	 * 缓冲区容量
	 */
	private int capacity;

	public CompositeBuffer() {
		Component component = ComponentFactory.borrowBuffer(0);
		components[0] = component;
		buffers[0] = component.get();
		buf = buffers[0];
		capacity = buf.length;
	}

	@Override
	public void write(char value) throws IOException {
		buf[writerIndex++] = value;
	}

	@Override
	public void safeWrite(char value) throws IOException {
		if ((writerIndex >= capacity)) {
			// 切换缓冲区
			bufferIndex++;
			writerIndex = 0;
			buf = buffers[bufferIndex]; // 设置当前缓冲区
			capacity = buf.length; // 设置当前缓冲区容量
		}
		buffers[bufferIndex][writerIndex++] = value;
	}

	@Override
	public boolean ensureWritable(int writable) {
		if (writable + writerIndex > capacity) {
			return expandCapacity(writable + capacity);
		}
		return false;
	}

	/**
	 * 缓冲区扩容
	 * <p>
	 * 使用{@code 256k}划分，当前容量小于{@code 256k}时，使用new-copy对当前缓冲区扩容，
	 * 当容量大于{@code 256k}时，增加缓冲区的方式进行扩容。
	 * <p>
	 * 常规缓冲区：常规缓冲区中只有一个缓冲区，当对缓冲区进行扩容时，直接将缓冲区容量进行翻倍。
	 * 优点：写入速度更快，缺点：大容量缓冲区容量翻倍时对内存造成过大压力。
	 * <p>
	 * 复合缓冲区：复合缓冲区中有多个缓冲区，当一个缓冲区中的数据写满时，开始在第二个缓冲区中写入。
	 * 优点：拓容方式更灵活，缺点：跨缓冲区写入时性能下降。
	 *
	 * @param minCapacity 需要的最小容量
	 * @return 是否添加缓冲区。{@code true}添加缓冲区，{@code false}当前缓冲区。
	 */
	private boolean expandCapacity(int minCapacity) {
		if (bufferIndex < CodecConfig.BUFFER_LOCAL_SIZE && capacity < CodecConfig.BUFFER_LOCAL_MAX_CAPACITY) {
			// 4k -> 16k -> 64k -> 256k
			int newCapacity = capacity << 2;
			buf = Arrays.copyOf(buf, newCapacity);
			capacity = newCapacity;
			buffers[bufferIndex] = buf;
			return false;
		} else {
			int bufferIndex = this.bufferIndex + 1;
			if (bufferIndex >= components.length || components[bufferIndex] == null) {
				int capacity = this.capacity;
				while (capacity < minCapacity) {
					if (bufferIndex >= buffers.length) {
						// 复合缓冲区容量不足
						buffers = Arrays.copyOf(buffers, buffers.length << 1);
						components = Arrays.copyOf(components, components.length << 1);
					}
					Component component = ComponentFactory.borrowBuffer(bufferIndex);
					components[bufferIndex] = component;
					char[] newBuffer = component.get();
					buffers[bufferIndex++] = newBuffer;
					capacity += newBuffer.length;
				}
			}
		}
		return true;
	}

	@Override
	public void close() throws IOException {
		for (int i = 0, limit = buffers.length; i < limit; i++) {
			Component component = components[i];
			if (component == null) {
				break;
			}
			// 基础缓存不处理
			if (component.isBasic()) {
				continue;
			}
			ComponentFactory.returnBuffer(component);
			components[i] = null;
			buffers[i] = null;
		}
		writerIndex = 0;
		bufferIndex = 0;
	}

	/**
	 * 写入指定位置
	 *
	 * @param <T>       返回值类型
	 * @param generator 写入策略
	 * @param dest      写入目标
	 * @param offset    偏移位置
	 * @return 返回内容
	 * @throws WriteFailedException 写入失败异常
	 */
	@Override
	public <T> T writeTo(Generator<T> generator, T dest, int offset) throws WriteFailedException {
		int length = bufferIndex * CodecConfig.BUFFER_LOCAL_MAX_CAPACITY + writerIndex;
		return generator.generate(dest, offset, buffers, length, bufferIndex, writerIndex);
	}

	public interface Component {

		/**
		 * 设置缓冲区缓存
		 *
		 * @param buf 缓冲区
		 */
		void set(char[] buf);

		/**
		 * 获取缓冲区缓存
		 *
		 * @return 缓冲区
		 */
		char[] get();

		/**
		 * 基础缓存
		 *
		 * @return 是否缓存
		 */
		boolean isBasic();

		/**
		 * 标记一个缓冲区正在使用
		 */
		void mark();

		/**
		 * 释放缓冲区
		 */
		void release();
	}

	public static class ComponentFactory {

		public static final int BUFFER_CACHE_SIZE = PlatformUtil.cores() * 4; // 按照每个核心1M缓存设定

		private static final RingQueue<Component> queue = new RingQueue<Component>(BUFFER_CACHE_SIZE);

		public static Component borrowBuffer(int bufferIndex) {
			if (bufferIndex < CodecConfig.BUFFER_LOCAL_SIZE) {
				return new StrongComponent(CodecConfig.BUFFER_LOCAL_MIN_CAPACITY);
			}
			Component cache = queue.dequeue();
			if (cache == null) {
				cache = new SoftComponent(CodecConfig.BUFFER_LOCAL_MAX_CAPACITY);
			}
			cache.mark();
			return cache;
		}

		public static void returnBuffer(@NotNull Component buffer) {
			buffer.release();
			queue.enqueue(buffer);
		}
	}

	public static class SoftComponent extends SimpleReference<char[]> implements Component {

		int length;

		/**
		 * 确保在使用中，对象不会被回收
		 */
		char[] strong;

		public SoftComponent(int length) {
			super(new char[length]);
			this.length = length;
		}

		@Override
		public boolean isBasic() {
			return false;
		}

		@Override
		public void mark() {
			strong = get(); // 增加一个强引用
			if (strong == null) { // 对象已经被回收，重新设置一个值
				strong = new char[length];
				super.set(strong);
			}
		}

		@Override
		public void release() {
			strong = null; // 释放强引用
		}
	}

	public static class StrongComponent implements Component {

		private char[] cache;

		public StrongComponent(int length) {
			cache = new char[length];
		}

		@Override
		public void set(char[] buf) {
			cache = buf;
		}

		@Override
		public char[] get() {
			return cache;
		}

		@Override
		public boolean isBasic() {
			return true;
		}

		@Override
		public void mark() {

		}

		@Override
		public void release() {
			cache = null;
		}
	}
}