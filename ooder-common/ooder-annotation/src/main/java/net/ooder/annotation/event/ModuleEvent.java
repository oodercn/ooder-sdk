package net.ooder.annotation.event;


import net.ooder.annotation.CustomAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
public @interface ModuleEvent {

    String eventName() default "";

    ModuleEventEnum event();

    CustomAction[] actions() default {};

    String desc() default "";

    boolean _return() default true;

    String eventReturn() default "{true}";
}
