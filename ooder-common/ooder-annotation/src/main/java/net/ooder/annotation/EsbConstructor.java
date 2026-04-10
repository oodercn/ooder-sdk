package net.ooder.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Inherited
@Retention(value=RUNTIME)
@Target({ ElementType.CONSTRUCTOR})
public @interface EsbConstructor {
	
	public String id() default "";
	
	public String expressionArr() default "";

	public  int version() default  0;
}
