/*
 *@(#)Log.java 2025-08-25
 *
 *Copyright (c) ooder. All rights reserved. 
 */
package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 日志自定义注解类
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
public @interface JDSLog {
	/**
	 * level
	 */
	LogLevel level() default LogLevel.INFO;

}
