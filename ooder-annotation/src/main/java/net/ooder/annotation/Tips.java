package net.ooder.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Tips {

    String mask() default "";

    String tipsErr() default "";

    String tipsBinder() default "";

    String tips() default "";

    String tipsOK() default "";


}
