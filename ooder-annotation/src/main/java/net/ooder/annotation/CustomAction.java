package net.ooder.annotation;


import net.ooder.annotation.event.ActionTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface CustomAction {

    String desc() default "";

    ActionTypeEnum type() default ActionTypeEnum.other;

    String name() default "";

    String script() default "";

    String[] params() default {};

    String expression() default "true";

    String target() default "";

    String className() default "";

    String childName() default "";

    String method() default "";

    boolean _return() default true;

    String redirection() default "";

    String okFlag() default "";

    String koFlag() default "";

    CustomCondition[] conditions() default {};

    String[] args() default {};


}
