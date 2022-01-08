package com.keimons.deepjson.annotation;

import com.keimons.deepjson.util.DeductionFailedException;
import com.keimons.deepjson.util.IllegalAnnotationException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定构造方法
 * <p>
 * 用于指定实例化时使用的构造方法，该注解在一个类中最多有一个（暂不提供对于工厂方法的支持）。
 * 有两种方式使用这个注解，属性名称自动推导和属性名称直接指定，例如：
 * <p>
 * 属性名称自动推导：
 * <pre>
 * &#064;CodecCreator()
 * public Node(String name, int age) {}
 * </pre>
 * 属性名称直接指定：
 * <pre>
 * &#064;CodecCreator("name", "age")
 * public Node(String name, int age) {}
 * </pre>
 * 注意：属性名称全指定或全推导，不能部分指定 + 部分推导。DeepJson使用将会读取该类的<code>.class</code>
 * 文件字节码，并从中解析出标注<code>@CodecCreator</code>
 * 注解的构造方法的字段名称。
 *
 * @author houyn[monkey@keimons.com]
 * @version 1.0
 * @see ClassCastException 属性类型不兼容
 * @see DeductionFailedException 属性名称推导失败
 * @see IllegalAnnotationException 类中出现超过一个注解
 * @since 1.6
 **/
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@CodecAnnotation
public @interface CodecCreator {

	/**
	 * 属性名称
	 * <p>
	 * 调用构造方法时，使用指定的属性。
	 * 如果没有指定属性名称，默认按照构造方法的参数名称作为属性名称。
	 * 如果明确指定属性名称，需要确保构造方法参数和属性数量相同。
	 *
	 * @return 属性名称
	 * @see IllegalAnnotationException 属性名称数量与构造方法参数数量不符
	 */
	String[] value() default {};
}