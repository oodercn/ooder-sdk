package net.ooder.annotation;

import net.ooder.annotation.ui.ResponsePathTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ResponsePathAnnotation {
    ResponsePathTypeEnum type();

    String paramsname();

    String path() default "";


}
