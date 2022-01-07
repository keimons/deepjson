package com.keimons.deepjson.internal.util;

import com.keimons.deepjson.util.ClassUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 字节码工具类
 * <p>
 * 此api为内部api，仅获取构造方法参数名，不对外开放使用。未来版本中可能会支持更多方法。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 */
public class BytecodeUtils {

	/**
	 * 构造方法
	 */
	private static final String CONSTRUCTOR = "<init>";

	/**
	 * 代码标识
	 */
	private static final String CODE = "Code";

	/**
	 * 局部变量表中的第一个变量名
	 */
	private static final String THIS = "this";

	/**
	 * 局部变量表
	 */
	private static final String LOCAL_VARIABLE_TABLE = "LocalVariableTable";

	/**
	 * 被支持最低class版本
	 */
	private static final int VERSION_MIN = 50;

	/**
	 * 被支持最高class版本
	 */
	private static final int VERSION_MAX = 63;

	/**
	 * 获取类的字节码
	 *
	 * @param clazz 获取字节码的类
	 * @return 该类的字节码
	 * @throws InternalIgnorableException 读取字节码中的异常
	 */
	public static byte[] findBytecodes(Class<?> clazz) throws InternalIgnorableException {
		String name = ClassUtil.getClassFileName(clazz);
		InputStream is = clazz.getResourceAsStream(name);
		if (is == null) {
			throw new InternalIgnorableException("cannot find '.class' file for class [" + clazz + "]");
		}
		try {
			return readStream(is);
		} catch (Exception ex) {
			throw new InternalIgnorableException("cannot read '.class' file for class [" + clazz + "]", ex);
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				// ignore
			}
		}
	}

	/**
	 * 读取输入流并将其内容作为字节数组返回
	 *
	 * @param is 输入流
	 * @return 输入流的内容
	 * @throws IOException 读取中的异常
	 */
	private static byte[] readStream(final InputStream is) throws IOException {
		int bufferSize = calculateBufferSize(is);
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			byte[] data = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = is.read(data, 0, bufferSize)) != -1) {
				os.write(data, 0, bytesRead);
			}
			os.flush();
			return os.toByteArray();
		}
	}

	/**
	 * 缓冲区最大容量
	 */
	private static final int MAX_BUFFER_SIZE = 1024 * 1024;

	/**
	 * 缓冲区最小容量
	 */
	private static final int MIN_BUFFER_SIZE = 4096;

	/**
	 * 计算临时缓冲区长度
	 *
	 * @param is 输入流
	 * @return 临时缓冲区长度
	 * @throws IOException 读取中的异常
	 */
	private static int calculateBufferSize(final InputStream is) throws IOException {
		int available = is.available();
		if (available < MIN_BUFFER_SIZE) {
			return MIN_BUFFER_SIZE;
		}
		return Math.min(available, MAX_BUFFER_SIZE);
	}

	/**
	 * 在指定的缓冲区中查找构造方法的参数名
	 *
	 * @param buf  缓冲区
	 * @param desc 构造方法签名
	 * @return 构造方法的参数名
	 * @throws InternalIgnorableException 查找构造方法参数名时发生的异常，该异常强制捕获，并需要提供替代方案
	 */
	public static String[] findConstructorParameterNames(final byte[] buf, String desc) throws InternalIgnorableException {
		// skip magic(4), minor_version(2)
		int majorVersion = readUnsignedShort(buf, 6);
		if (majorVersion < VERSION_MIN || VERSION_MAX < majorVersion) {
			throw new InternalIgnorableException("Unsupported class file major version " + majorVersion);
		}
		// constant_pool_count. skip magic(4), minor_version(2), major_version(2)
		int count = readUnsignedShort(buf, 8);
		int[] constants = new int[count];
		// the 0th in the constant_pool_count is a special value, starting from 1
		int index = 1;
		// skip magic(4), minor_version(2), major_version(2), constant_pool_count(2)
		int offset = 10;
		// record and skip entries
		while (index < count) {
			// tag of constant pool entries
			byte tag = buf[offset++];
			constants[index++] = offset;
			int size;
			switch (tag) {
				case ConstantTag.CONSTANT_FIELDREF_TAG:
				case ConstantTag.CONSTANT_METHODREF_TAG:
				case ConstantTag.CONSTANT_INTERFACE_METHODREF_TAG:
				case ConstantTag.CONSTANT_INTEGER_TAG:
				case ConstantTag.CONSTANT_FLOAT_TAG:
				case ConstantTag.CONSTANT_NAME_AND_TYPE_TAG:
				case ConstantTag.CONSTANT_DYNAMIC_TAG:
				case ConstantTag.CONSTANT_INVOKE_DYNAMIC_TAG:
					size = 4;
					break;
				case ConstantTag.CONSTANT_LONG_TAG:
				case ConstantTag.CONSTANT_DOUBLE_TAG:
					// double byte
					size = 8;
					break;
				case ConstantTag.CONSTANT_UTF8_TAG:
					// length(2), bytes
					size = 2 + readUnsignedShort(buf, offset);
					break;
				case ConstantTag.CONSTANT_METHOD_HANDLE_TAG:
					// reference_kind(1), reference_type(2)
					size = 3;
					break;
				case ConstantTag.CONSTANT_CLASS_TAG:
				case ConstantTag.CONSTANT_STRING_TAG:
				case ConstantTag.CONSTANT_METHOD_TYPE_TAG:
				case ConstantTag.CONSTANT_PACKAGE_TAG:
				case ConstantTag.CONSTANT_MODULE_TAG:
					size = 2;
					break;
				default:
					throw new InternalIgnorableException("unknown constant pool tag: " + tag);
			}
			offset += size;
		}

		// skip interfaces(length * 2)
		offset += readUnsignedShort(buf, offset + 6) * 2;
		// skip access_flags(2), this_class(2), super_class(2), interface_count(2)
		offset += 8;

		// skip the fields
		int fields = readUnsignedShort(buf, offset);
		offset += 2;
		for (int i = 0; i < fields; i++) {
			offset = skipField(buf, offset);
		}
		Node node = new Node();
		// visit the methods
		int methods = readUnsignedShort(buf, offset);
		offset += 2;
		for (int i = 0; i < methods; i++) {
			offset = readMethod(buf, constants, offset, desc, node);
			if (offset == -1) {
				break;
			}
		}
		return node.value;
	}

	/**
	 * 跳过一个字段
	 *
	 * @param buf    缓冲区
	 * @param offset 字段结构起始位置
	 * @return 字段结构结束位置
	 */
	private static int skipField(byte[] buf, int offset) {
		offset += 6; // skip access_flags(2), name_index(2) and descriptor_index(2).
		int count = readUnsignedShort(buf, offset);
		offset += 2;
		for (int i = 0; i < count; i++) {
			int length = readInt(buf, offset + 2);
			offset += 6; // skip attribute_name(2) and attribute_length(4).
			offset += length; // skip attributes(length)
		}
		return offset;
	}

	/**
	 * 读取方法结构并使用指定的访问者访问它
	 *
	 * @param buf       缓冲区
	 * @param constants 常量池
	 * @param offset    方法的起始偏移位置
	 * @return 方法结构后的第一个位置
	 * @throws InternalIgnorableException 方法读取异常
	 */
	private static int readMethod(byte[] buf, int[] constants, int offset, String desc, Node node) throws InternalIgnorableException {
		String methodName = readUTF8(buf, constants, offset + 2);
		String methodDesc = readUTF8(buf, constants, offset + 4);
		// skip access_flags(2), name_index(2), descriptor_index(2)
		offset += 6;
		int codeOffset = 0;
		int count = readUnsignedShort(buf, offset);
		offset += 2;
		for (int i = 0; i < count; i++) {
			String name = readUTF8(buf, constants, offset);
			int length = readInt(buf, offset + 2);
			// skip attribute_name(2), attribute_length(4)
			offset += 6;
			if (CODE.equals(name)) {
				codeOffset = offset;
			}
			// skip attribute_entries(length)
			offset += length;
		}

		if (CONSTRUCTOR.equals(methodName) && desc.equals(methodDesc)) {
			if (codeOffset == 0) {
				throw new InternalIgnorableException("no code of constructor: " + desc);
			}
			node.value = findConstructorParameterNames(buf, constants, codeOffset);
			offset = -1;
		}
		return offset;
	}

	/**
	 * 读取代码，查找方法参数名称
	 *
	 * @param buf       缓冲区
	 * @param constants 常量池
	 * @param offset    相对于缓冲区起始位置的偏移量
	 * @return 方法参数名称
	 * @throws InternalIgnorableException 可以忽略的异常
	 */
	private static String[] findConstructorParameterNames(byte[] buf, int[] constants, int offset) throws InternalIgnorableException {
		final int cl = readInt(buf, offset + 4);
		// skip max_stack(2), max_local(2), code_length(4)
		offset += 8;
		if (cl > buf.length - offset) {
			// unknown end of file
			throw new InternalIgnorableException("EOF");
		}
		// skip codes
		offset += cl;

		final int el = readUnsignedShort(buf, offset);
		// skip exception_table_length(2)
		offset += 2;
		// skip exceptions
		offset += el * 8;

		int variableOffset = 0;

		int count = readUnsignedShort(buf, offset);
		offset += 2;
		while (count-- > 0) {
			String name = readUTF8(buf, constants, offset);
			int length = readInt(buf, offset + 2);
			// skip attribute_name(2) attribute_length(4)
			offset += 6;
			if (LOCAL_VARIABLE_TABLE.equals(name)) {
				variableOffset = offset;
			}
			// skip attribute(length)
			offset += length;
		}

		if (variableOffset == 0) {
			throw new InternalIgnorableException("the method don't have local variable");
		}

		int length = readUnsignedShort(buf, variableOffset);
		String[] names = new String[length - 1]; // not static, 0th is always "this"
		offset = variableOffset + 2;
		while (length-- > 0) {
			String name = readUTF8(buf, constants, offset + 4);
			int index = readUnsignedShort(buf, offset + 8);
			offset += 10;
			if (!THIS.equals(name)) {
				names[index - 1] = name;
			}
		}
		return names;
	}

	/**
	 * 读取一个无符号{@code short}值
	 *
	 * @param buf    缓冲区
	 * @param offset 相对于缓冲区起始的偏移位置
	 * @return 无符号{@code short}值
	 */
	private static int readUnsignedShort(byte[] buf, int offset) {
		return ((buf[offset] & 0xFF) << 8) | (buf[offset + 1] & 0xFF);
	}

	/**
	 * 读取一个有符号{@code int}值
	 *
	 * @param buf    缓冲区
	 * @param offset 相对于缓冲区起始的偏移位置
	 * @return 有符号{@code int}值
	 */
	private static int readInt(byte[] buf, final int offset) {
		return ((buf[offset] & 0xFF) << 24) | ((buf[offset + 1] & 0xFF) << 16) | ((buf[offset + 2] & 0xFF) << 8) | (buf[offset + 3] & 0xFF);
	}

	/**
	 * 读取常量池中的数据
	 *
	 * @param buf       缓冲区
	 * @param constants 常量池
	 * @param offset    起始偏移量（其值是常量池的索引，例如：#1）。
	 * @return 常量池中的字符串
	 */
	private static String readUTF8(byte[] buf, int[] constants, int offset) {
		int index = readUnsignedShort(buf, offset);
		if (offset == 0 || index == 0) {
			return null;
		}
		offset = constants[index];
		int length = readUnsignedShort(buf, offset);
		return readUtf(buf, offset + 2, length);
	}

	/**
	 * 使用UTF8编码读取{@code buf}中的字符串
	 *
	 * @param buf    缓冲区
	 * @param offset 字符串的起始位置
	 * @param length 字符串的长度
	 * @return UTF8编码的字符串
	 */
	private static String readUtf(byte[] buf, int offset, final int length) {
		int limit = offset + length;
		int index = 0;
		char[] buffer = new char[length];
		while (offset < limit) {
			int b = buf[offset++];
			if ((b & 0x80) == 0) {
				buffer[index++] = (char) (b & 0x7F);
			} else if ((b & 0xE0) == 0xC0) {
				buffer[index++] = (char) (((b & 0x1F) << 6) + (buf[offset++] & 0x3F));
			} else {
				buffer[index++] = (char) (((b & 0xF) << 12) | ((buf[offset++] & 0x3F) << 6) | (buf[offset++] & 0x3F));
			}
		}
		return new String(buffer, 0, index);
	}

	/**
	 * 包装容器，用于解决多返回值问题
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 */
	private static class Node {

		String[] value;
	}

	/**
	 * 常量池类型
	 *
	 * @author houyn[monkey@keimons.com]
	 * @version 1.0
	 * @since 1.6
	 */
	private static class ConstantTag {

		/**
		 * The tag value of CONSTANT_Utf8_info JVMS structures.
		 */
		static final int CONSTANT_UTF8_TAG = 1;

		/**
		 * The tag value of CONSTANT_Integer_info JVMS structures.
		 */
		static final int CONSTANT_INTEGER_TAG = 3;

		/**
		 * The tag value of CONSTANT_Float_info JVMS structures.
		 */
		static final int CONSTANT_FLOAT_TAG = 4;

		/**
		 * The tag value of CONSTANT_Long_info JVMS structures.
		 */
		static final int CONSTANT_LONG_TAG = 5;

		/**
		 * The tag value of CONSTANT_Double_info JVMS structures.
		 */
		static final int CONSTANT_DOUBLE_TAG = 6;

		/**
		 * The tag value of CONSTANT_Class_info JVMS structures.
		 */
		static final int CONSTANT_CLASS_TAG = 7;

		/**
		 * The tag value of CONSTANT_String_info JVMS structures.
		 */
		static final int CONSTANT_STRING_TAG = 8;

		/**
		 * The tag value of CONSTANT_Fieldref_info JVMS structures.
		 */
		static final int CONSTANT_FIELDREF_TAG = 9;

		/**
		 * The tag value of CONSTANT_Methodref_info JVMS structures.
		 */
		static final int CONSTANT_METHODREF_TAG = 10;

		/**
		 * The tag value of CONSTANT_InterfaceMethodref_info JVMS structures.
		 */
		static final int CONSTANT_INTERFACE_METHODREF_TAG = 11;

		/**
		 * The tag value of CONSTANT_NameAndType_info JVMS structures.
		 */
		static final int CONSTANT_NAME_AND_TYPE_TAG = 12;

		/**
		 * The tag value of CONSTANT_MethodHandle_info JVMS structures.
		 */
		static final int CONSTANT_METHOD_HANDLE_TAG = 15;

		/**
		 * The tag value of CONSTANT_MethodType_info JVMS structures.
		 */
		static final int CONSTANT_METHOD_TYPE_TAG = 16;

		/**
		 * The tag value of CONSTANT_Dynamic_info JVMS structures.
		 */
		static final int CONSTANT_DYNAMIC_TAG = 17;

		/**
		 * The tag value of CONSTANT_InvokeDynamic_info JVMS structures.
		 */
		static final int CONSTANT_INVOKE_DYNAMIC_TAG = 18;

		/**
		 * The tag value of CONSTANT_Module_info JVMS structures.
		 */
		static final int CONSTANT_MODULE_TAG = 19;

		/**
		 * The tag value of CONSTANT_Package_info JVMS structures.
		 */
		static final int CONSTANT_PACKAGE_TAG = 20;
	}
}