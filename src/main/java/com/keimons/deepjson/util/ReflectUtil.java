package com.keimons.deepjson.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;
import java.util.StringJoiner;

/**
 * DeepJson配置文件
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @since 1.6
 **/
public class ReflectUtil {

	/**
	 * 构造一个参数化类型
	 *
	 * @param ownerType 归属类，用于定位这个泛型的归属（DeepJson不会使用这个字段，允许为{@code null}）。
	 * @param rawType   原始类型，例如：{@link Map}。
	 * @param arguments 参数列表，例如：{@code K}和{@code V}。
	 * @return 参数化类型
	 */
	public static ParameterizedType makeParameterizedType(@Nullable Type ownerType, Class<?> rawType, Type[] arguments) {
		return new CodecParameterizedType(ownerType == null ? rawType.getDeclaringClass() : ownerType, rawType, arguments);
	}

	/**
	 * 构造一个类型变量
	 *
	 * @param decl   所属类
	 * @param name   变量名，例如：{@code K}和{@code V}。
	 * @param bounds 上界
	 * @param <D>    所属类类型。
	 * @return 类型变量
	 */
	public static <D extends GenericDeclaration> TypeVariable<D> makeTypeVariable(D decl, String name, Type[] bounds) {
		return new CodecTypeVariable<D>(decl, name, bounds);
	}

	/**
	 * 构造一个通配符类型
	 *
	 * @param upperBounds 通配符上界，例如：? extend Number
	 * @param lowerBounds 通配符下界，例如：? super LinkedHashMap
	 * @return 通配符类型
	 */
	public static WildcardType makeWildcardType(Type[] upperBounds, @Nullable Type[] lowerBounds) {
		return new CodecWildcardType(upperBounds, lowerBounds == null ? new Type[0] : lowerBounds);
	}

	/**
	 * 参数化类型
	 */
	private static class CodecParameterizedType implements ParameterizedType {

		/**
		 * 参数列表
		 */
		private final Type[] arguments;

		/**
		 * 所属类
		 */
		private final Class<?> rawType;

		/**
		 * 归属类
		 */
		private final Type ownerType;

		public CodecParameterizedType(Type ownerType, Class<?> rawType, Type[] arguments) {
			TypeVariable<?>[] variables = rawType.getTypeParameters();
			// 参数列表需要一样长
			if (variables.length != arguments.length) {
				throw new MalformedParameterizedTypeException();
			}
			this.ownerType = ownerType;
			this.rawType = rawType;
			this.arguments = arguments;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return arguments;
		}

		@Override
		public Type getRawType() {
			return rawType;
		}

		@Override
		public Type getOwnerType() {
			return ownerType;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (ownerType != null) {
				sb.append(ownerType.getTypeName());
				sb.append("$");
				if (ownerType instanceof CodecParameterizedType) {
					sb.append(rawType.getName().replace(((CodecParameterizedType) ownerType).rawType.getName() + "$", ""));
				} else {
					sb.append(rawType.getSimpleName());
				}
			} else {
				sb.append(rawType.getName());
			}

			if (arguments != null) {
				StringJoiner sj = new StringJoiner(", ", "<", ">");
				sj.setEmptyValue("");
				for (Type t : arguments) {
					sj.add(t.getTypeName());
				}
				sb.append(sj);
			}

			return sb.toString();
		}
	}

	/**
	 * 仅实现类型变量中的部分，如果使用{@link Annotation}部分，会抛出{@link UnsupportedOperationException}异常。
	 *
	 * @param <D> 参数
	 */
	private static class CodecTypeVariable<D extends GenericDeclaration> implements TypeVariable<D> {

		/**
		 * 参数描述符
		 */
		private final D genericDeclaration;

		/**
		 * 参数名，例如：{@code K}
		 */
		private final String name;

		/**
		 * 上界
		 */
		private final Type[] bounds;

		public CodecTypeVariable(D decl, String name, Type[] bounds) {
			this.genericDeclaration = decl;
			this.name = name;
			this.bounds = bounds;
		}

		@Override
		public Type[] getBounds() {
			return bounds.clone();
		}

		@Override
		public D getGenericDeclaration() {
			return genericDeclaration;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public AnnotatedType[] getAnnotatedBounds() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T extends Annotation> T getAnnotation(@NotNull Class<T> annotationClass) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Annotation[] getAnnotations() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return getName();
		}
	}

	private static class CodecWildcardType implements WildcardType {

		/**
		 * 上界
		 */
		private final Type[] upperBounds;

		/**
		 * 下界
		 */
		private final Type[] lowerBounds;

		public CodecWildcardType(Type[] upperBounds, Type[] lowerBounds) {
			this.upperBounds = upperBounds;
			this.lowerBounds = lowerBounds;
		}

		@Override
		public Type[] getUpperBounds() {
			return upperBounds.clone();
		}

		@Override
		public Type[] getLowerBounds() {
			return lowerBounds.clone();
		}

		public String toString() {
			Type[] lowerBounds = getLowerBounds();
			Type[] bounds = lowerBounds;
			StringBuilder sb = new StringBuilder();

			if (lowerBounds.length > 0) {
				sb.append("? super ");
			} else {
				Type[] upperBounds = getUpperBounds();
				if (upperBounds.length > 0 && !upperBounds[0].equals(Object.class)) {
					bounds = upperBounds;
					sb.append("? extends ");
				} else {
					return "?";
				}
			}

			StringJoiner sj = new StringJoiner(" & ");
			for (Type bound : bounds) {
				sj.add(bound.getTypeName());
			}
			sb.append(sj);

			return sb.toString();
		}
	}
}