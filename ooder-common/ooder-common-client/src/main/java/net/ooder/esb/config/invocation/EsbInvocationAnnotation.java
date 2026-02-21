/**
 * $RCSfile: EsbInvocationAnnotation.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.esb.config.invocation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Inherited
@Retention(value=RUNTIME)
@Target(value=TYPE)
public @interface EsbInvocationAnnotation {
	
	public String id() default "";
	
	public String expressionArr() default "";
	
	public String filter() default "";
	
	public String name() default "";
		
	public String desc() default "";
	
	public String flowType() default "";
	
	public String clazz() default "";
	
	public String dataType() default "action";
	
	public String jspUrl() default "";
	public  int version() default  0;
}
