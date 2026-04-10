/*
 *@(#)Access.java 2025-08-25
 *
 *Copyright (c) ooder. All rights reserved. 
 */
package net.ooder.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口访问级别注解
 * 
 * @author ooder
 * @since 2025-08-25
 * @version <1.0>
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Access {
	/**
	 * session验证
	 */
	boolean session() default false;

	/**
	 * session验证
	 */
	boolean ip() default false;

	/**
	 * session验证
	 */
	boolean local() default false;

}
